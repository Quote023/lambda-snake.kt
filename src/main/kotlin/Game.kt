import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.*
import org.w3c.dom.events.KeyboardEvent
import kotlin.math.max


/**
 * Ponto de entrada do software, inicializa o jogo no momento que a página é carregada por completo
 */
fun main() {
    window.onload = { gameStart() }
}

const val CANVAS_SCALE = 10

/**
 * PARTE IMPURA.
 * estado da última tecla de direção apertada pelo usuário.
 * modificado no evento "keydown" definido no [gameStart]
 */
var desiredDir = Direction.LEFT

fun gameStart(){
  //Inicializa o canvas principal.
  val canvas = document.getElementById("game-canvas") as HTMLCanvasElement
  val context = canvas.getContext("2d")!! as CanvasRenderingContext2D
  context.clearRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())
  
  //Calcula as dimensões da tela de jogo.
  val canvasDimensions = Dimensions(canvas.width,canvas.height)
  val boardDimensions = canvasDimensions / CANVAS_SCALE
  
  //Gatilho disparado sempre que qualquer tecla seja pressionada no teclado do usuário
  window.addEventListener("keydown",{event -> (event as KeyboardEvent).let {
   /** Caso a tecla pressionada seja um comando de movimento válido, redefine a [desiredDir] para a direção escolhida **/
    when(it.key){
     "ArrowUp","w"    -> { desiredDir = Direction.UP }
     "ArrowDown","s"  -> { desiredDir = Direction.DOWN }
     "ArrowLeft","a"  -> { desiredDir = Direction.LEFT }
     "ArrowRight","d" -> { desiredDir = Direction.RIGHT }
     else -> Unit
   } 
  }})
  
  //Incializa um tabuleiro aleatório
  //TODO: parametrizar esse tamanho?
  val board = initRandomBoard(boardDimensions.width,boardDimensions.height)

  console.log(board)
  console.log(board.toString())
  //Começa o loop de jogo, isso é uma função recursiva que roda indefinidamente até que o jogo acabe
  
  gameLoop(board,context,canvasDimensions)
}



/**
 * @param boardState lista com a posição e estado atual de todas as peças no tabuleiro
 * @param context usado para imprimir as informações do tabuleiro na tela **(pintar os pixeis no canvas)**
 * @param canvasDim dimensões do canvas **(Definido na primeira iteração, não mudar)**
 */
fun gameLoop(boardState: Board, context: CanvasRenderingContext2D,canvasDim: Dimensions){
  /**
  * Caso a direção do player seja alterada entre as renderizações (AÇÃO IMPURA)
  * gera uma cópia da peça do jogador com essa informação atualizada e usa ela no lugar
  */
  val player = boardState.player.let { 
    if(desiredDir == it.direction || it.direction.isInverse(desiredDir)) it
    else it.copy(direction = desiredDir)
  }
  
  //Imprime no canvas a tela atual
  renderBoard(context,boardState)
  
  if(player.life <= 0){
    endGame(context, canvasDim,boardState)
    return
  }
  
  val newBoard = movePlayer(boardState,player)
  window.setTimeout({gameLoop(newBoard,context,canvasDim)}, max(10,(100 - (2 * player.tail.size))))
  currentScore(player)
}

fun endGame(context: CanvasRenderingContext2D, canvasDim: Dimensions,boardState: Board){
  context.fillStyle = "#000000CC"
  context.shadowBlur = 0.5
  context.fillRect(0.0, 0.0, canvasDim.width.toDouble(),canvasDim.height.toDouble())
  
  context.font = "30px Comic Sans MS"
  context.fillStyle = "red"
  val msg = "PERDEU OTÁRIO"
  val msgSize = context.measureText(msg).width
  
  console.log(boardState.toString())
  context.fillText(msg, (canvasDim.width / 2.0) - (msgSize/2), canvasDim.height / 2.0)
}
