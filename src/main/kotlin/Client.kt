import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.random.Random

fun main() {
    window.onload = { gameLoop() }
}

open class Tile(val x: Number, val y: Number)

class Blank(x:Number, y: Number): Tile(x,y)
class Player(x: Number, y: Number): Tile(x,y)
class Fruit(x: Number, y: Number): Tile(x,y)

fun gameLoop(){
  val canvas = document.getElementById("game-canvas") as HTMLCanvasElement
  val context = canvas.getContext("2d")!! as CanvasRenderingContext2D
  context.clearRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())
  val cWidth = canvas.width
  val cHeight = canvas.height
  val boardWidth = cWidth / 10
  val boardHeight = cHeight / 10
  
  
  val board = initRandomBoard(30,30)
  
  renderBoard(context,board,boardWidth,boardHeight)
}


fun renderBoard(context: CanvasRenderingContext2D, board:List<Tile>, boardWidth: Int, boardHeight: Int){
  for (i in 0 until boardWidth){
    for (j in 0 until boardHeight){
      val index = i * boardWidth + j;
      val tile = board[index]
      context.fillStyle = when(tile) {
        is Player -> "green"
        is Fruit -> "red"
        else -> "white"
      }
      context.fillRect(i.toDouble() * 10, j.toDouble() * 10, 10.0,10.0)
    }
  }
}

fun initRandomBoard(width: Int, height: Int): List<Tile> {
  val rng = Random.Default
  val playerPos = Pair(rng.nextInt(width), rng.nextInt(height))
  val fruitPos = Pair(rng.nextInt(width), rng.nextInt(height))
  if(playerPos == fruitPos) return initRandomBoard(width,height)
  
    return (0 until (width * height)).map {  pos -> 
      with(Pair(pos % width,pos/width)) {
        when (this) {
          playerPos -> Player(this.first,this.second)
          fruitPos -> Fruit(this.first,this.second)
          else -> Blank(this.first,this.second)
        }
      }
    }
}

