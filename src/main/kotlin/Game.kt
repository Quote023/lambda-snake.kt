import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.KeyboardEvent
import kotlin.random.Random

/**
 * Ponto de entrada do software, inicializa o jogo no momento que a página é carregada por completo
 */
fun main() {
    window.onload = { gameStart() }
}

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
  val cWidth = canvas.width
  val cHeight = canvas.height
  val boardWidth = cWidth / 10
  val boardHeight = cHeight / 10
  
  //Gatilho disparado sempre que qualquer tecla seja pressionada no teclado do usuário
  window.addEventListener("keydown",{event -> (event as KeyboardEvent).let {
   /** Caso a tecla pressionada seja um comando de movimento válido, redefine a [desiredDir] para a direção escolhida **/
    when(it.key){
     "ArrowUp","w" -> { desiredDir = Direction.UP }
     "ArrowDown","s" -> { desiredDir = Direction.DOWN }
     "ArrowLeft","a" -> { desiredDir = Direction.LEFT }
     "ArrowRight","d" -> { desiredDir = Direction.RIGHT }
     else -> Unit
   } 
  }})
  
  //Incializa um tabuleiro aleatório de tamanho 30x30
  //TODO: parametrizar esse tamanho?
  val board = initRandomBoard(30,30)
  
  //Começa o loop de jogo, isso é uma função recursiva que roda indefinidamente até que o jogo acabe
  gameLoop(board,context,boardWidth,boardHeight)
}

/**
 * @param boardState lista com a posição e estado atual de todas as peças no tabuleiro
 * @param context usado para imprimir as informações do tabuleiro na tela **(pintar os pixeis no canvas)**
 * @param width largura do tabuleiro **(Definido na primeira iteração, não mudar)**
 * @param height altura do tabuleiro **(Definido na primeira iteração, não mudar)**
 */
fun gameLoop(boardState: List<Tile>, context: CanvasRenderingContext2D, width: Int,height: Int){
  /**
  * Caso a direção do player seja alterada entre as renderizações (AÇÃO IMPURA)
  * gera uma cópia da peça do jogador com essa informação atualizada e usa ela no lugar
  */
  val player = (boardState.find { it is Player }!! as Player).let { 
    if(desiredDir == it.direction) it
    else it.copy(direction =  desiredDir)
  }
  //Imprime no canvas a tela atual
  renderBoard(context,boardState,width,height)
  val newBoard = movePlayer(boardState,player)
  window.setTimeout({gameLoop(newBoard,context,width,height)}, 100)
}


/**
 * Calcula para qual posição o [player] deve se mover baseado na sua propriedade **player.direction**
 * Caso a posição calculada passe dos limites do tabuleiro, o player aparece na direção oposta da tela.
 */
fun movePlayer(board: List<Tile>, player: Player): List<Tile> {
  return when(player.direction){
    Direction.UP -> moveTile(board,player, Pair(player.x, clampTo0(player.y - 1, 29)))
    Direction.DOWN -> moveTile(board,player, Pair(player.x, clampToMax(player.y + 1, 29,0)))
    Direction.LEFT -> moveTile(board,player, Pair(clampTo0(player.x - 1, 29), player.y))
    Direction.RIGHT -> moveTile(board,player, Pair(clampToMax(player.x + 1, 29,0), player.y))
  }
}

/**
 * Move a casa [movedTile] para uma nova [posição desejada][newPos]
 * Define a posição original de [movedTile] como uma casa vazia
 * E cria uma cópia de [movedTile] na posição [newPos]
 */
fun moveTile(board: List<Tile>, movedTile: Tile, newPos: Pair<Int,Int>): List<Tile> {
  return board.map { 
    when(it.position){
      newPos -> movedTile.copy(it.x,it.y)
      movedTile.position -> Blank(it.x,it.y)
      else -> it
    }
  }
}

/**
* Posiciona a casa [target] na posição definida pela sua propriedade Position, remove a casa que estiver no lugar ignorando seus efeitos
*/
fun replaceTile(board: List<Tile>, target: Tile): List<Tile> {
  return board.map {
    when(it.position){
      target.position -> target
      else -> it
    }
  }
}


/**
 * Imprime no Canvas cada casa com sua respectiva cor
 */
fun renderBoard(context: CanvasRenderingContext2D, board:List<Tile>, boardWidth: Int, boardHeight: Int){
  //TODO: transformar esse for em um map ou fold
  //Percorre todas as casas do tabuleiro e desenha uma por uma na tela.
  for (y in 0 until boardWidth){
    for (x in 0 until boardHeight){
      val index = xyToIndex(x,y,boardWidth)
      val tile = board[index]
      
      //Define a cor do próximo quadrado a ser desenhado baseado em qual peça será impressa.
      context.fillStyle = when(tile) {
        is Player -> "green"
        is Fruit -> "red"
        else -> "white"
      }
      //Desenha um quadrado de 10x10 pixeis representando 1 casa do tabuleiro
      //o (x,y) é multiplicado por 10 pois o canvas está escalado em 10x (temos um tabuleiro de 30x30 em um canvas de 300x300)
      context.fillRect(y.toDouble() * 10, x.toDouble() * 10, 10.0,10.0)
    }
  }
}

/**
 * Gera um tabuleiro aleatório no tamanho especificado com 1 player e 1 fruta
 */
fun initRandomBoard(width: Int, height: Int): List<Tile> {
  val rng = Random.Default
  val playerPos = Pair(rng.nextInt(width), rng.nextInt(height))
  val fruitPos = Pair(rng.nextInt(width), rng.nextInt(height))
  
  //Caso os valores calculados sejam iguais, recalcula em uma nova posição até termos posições diferentes.
  if(playerPos == fruitPos) return initRandomBoard(width,height)
  
    return (0 until (width * height)).map {  pos -> 
      //Percorremos aqui todos os x/y possiveis
      //E instânciamos Players e Frutas em suas respectivas posições calculadas, todas as demais posições serão casas em branco.
      with(indexToXY(pos,width)) {
        when (this) {
          playerPos -> Player(this.first,this.second)
          fruitPos -> Fruit(this.first,this.second)
          else -> Blank(this.first,this.second)
        }
      }
    }
}

