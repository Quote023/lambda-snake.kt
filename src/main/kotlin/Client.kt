import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.random.Random

fun main() {
    window.onload = { gameStart() }
}

enum class Direction {
  Up,
  Down,
  Left,
  Right
}

open class Tile(val x: Int, val y: Int){
  val position by lazy { Pair(x,y) }
  open fun copy(x: Int = this.x, y: Int = this.y): Tile = Tile(x,y)
}

class Player(
  x: Int,
  y: Int,
  val direction: Direction = Direction.Left
): Tile(x,y){
  fun copy(x: Int = this.x, y: Int = this.y, direction: Direction = this.direction) = Player(x,y,direction)
  override fun copy(x: Int, y: Int) = Player(x,y,this.direction)
}

class Blank(
  x: Int,
  y: Int,
): Tile(x,y)

class Fruit(
  x: Int,
  y: Int,
): Tile(x,y)

fun gameStart(){
  val canvas = document.getElementById("game-canvas") as HTMLCanvasElement
  val context = canvas.getContext("2d")!! as CanvasRenderingContext2D
  context.clearRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())
  val cWidth = canvas.width
  val cHeight = canvas.height
  val boardWidth = cWidth / 10
  val boardHeight = cHeight / 10
  
  val board = initRandomBoard(30,30)
  gameLoop(board,context,boardWidth,boardHeight)
}

fun gameLoop( boardState: List<Tile>, context: CanvasRenderingContext2D, width: Int,height: Int){
  val player = (boardState.find { it is Player }!! as Player).let { 
    val chance = Random.Default.nextInt(100);
    when{
      chance < 25 -> it.copy(direction = Direction.Down)
      chance < 50 -> it.copy(direction = Direction.Left)
      chance < 75 -> it.copy(direction = Direction.Right)
      else -> it
    }
  }
  
  console.log(player)
  renderBoard(context,boardState,width,height)
  val newBoard = movePlayer(boardState,player)
  window.setTimeout(handler =  {gameLoop(newBoard,context,width,height)}, 200)
}




fun xyToIndex(x: Int,y: Int, width:Int) = x * width + y;
fun indexToXY(index: Int, width:Int) = Pair(index % width,index/width)

fun clampToMin(value: Int,min: Int, fallback: Int) = if (value >= min) value else fallback
fun clampTo0(value: Int, fallback: Int) = clampToMin(value, 0, fallback)
fun clampToMax(value: Int,max: Int, fallback: Int) = if (value <= max) value else fallback

fun movePlayer(board: List<Tile>, player: Player): List<Tile> {
  return when(player.direction){
    Direction.Up -> moveTile(board,player, Pair(player.x, clampToMax(player.y + 1, 29,0)))
    Direction.Down -> moveTile(board,player, Pair(player.x, clampTo0(player.y - 1, 29)))
    Direction.Left -> moveTile(board,player, Pair(clampTo0(player.x - 1, 29), player.y))
    Direction.Right -> moveTile(board,player, Pair(clampToMax(player.x + 1, 29,0), player.y))
  }
}

fun moveTile(board: List<Tile>, original: Tile, newPos: Pair<Int,Int>): List<Tile> {
  return board.map { 
    when(it.position){
      newPos -> original.copy(it.x,it.y)
      original.position -> Blank(it.x,it.y)
      else -> it
    }
  }
}

fun setTile(board: List<Tile>, target: Tile): List<Tile> {
  return board.map {
    when(it.position){
      target.position -> target
      else -> it
    }
  }
}

fun renderBoard(context: CanvasRenderingContext2D, board:List<Tile>, boardWidth: Int, boardHeight: Int){
  for (y in 0 until boardWidth){
    for (x in 0 until boardHeight){
      val index = xyToIndex(x,y,boardWidth)
      val tile = board[index]
      context.fillStyle = when(tile) {
        is Player -> "green"
        is Fruit -> "red"
        else -> "white"
      }
      context.fillRect(y.toDouble() * 10, x.toDouble() * 10, 10.0,10.0)
    }
  }
}

fun initRandomBoard(width: Int, height: Int): List<Tile> {
  val rng = Random.Default
  val playerPos = Pair(rng.nextInt(width), rng.nextInt(height))
  val fruitPos = Pair(rng.nextInt(width), rng.nextInt(height))
  if(playerPos == fruitPos) return initRandomBoard(width,height)
  
    return (0 until (width * height)).map {  pos -> 
      with(indexToXY(pos,width)) {
        when (this) {
          playerPos -> Player(this.first,this.second)
          fruitPos -> Fruit(this.first,this.second)
          else -> Blank(this.first,this.second)
        }
      }
    }
}

