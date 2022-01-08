@file:Suppress("unused")

/**
 * Representa as possiveis direções que o player pode ir:
 *  [UP];
 *  [DOWN];  
 *  [LEFT]; 
 *  [RIGHT];  
 */
enum class Direction {
  UP,
  DOWN,
  LEFT,
  RIGHT
}

fun Direction.isInverse(b: Direction): Boolean{
  val dirs = listOf(this,b)
  return dirs.containsAll(listOf(Direction.UP,Direction.DOWN)) || dirs.containsAll(listOf(Direction.LEFT,Direction.RIGHT)) 
}

data class RankingEntry(val name: String, val score: Int)


data class Board(
  val width: Int,
  val height: Int,
  val tiles: List<Tile> = listOf(),
  val player: Player = tiles.find { it is Player } as Player,
): List<Tile> by tiles {

  override fun toString(): String {
    val log = StringBuilder()
    (0 until (width * height)).forEach{ index ->
      when(this[index]) {
        is Player -> log.append("🟢")
        is PlayerBody -> log.append("🔷")
        is Fruit -> log.append("🟥")
        else -> log.append("⬜")
      }
      if(indexToXY(index,width).x >= width - 1) log.append("\n")
    }
    return log.toString()
  }
  
}

data class Position(val x:Int, val y:Int){
  operator fun div(b: Int): Position {
    return Position(x/b, y/b)
  }
}
data class Dimensions(val width: Int, val height: Int){
  operator fun div(b: Int): Dimensions {
    return Dimensions(width/b, height/b)
  }
}

/*
As classes aqui funcionam como uma simulação de tipo de dados algebraicos/compostos
*/

/**
 * Classe base de todas as casas do Tabuleiro
 * construtor marcado como protected pois em situações reais essa classe base nunca deve ser usada
 * (Caso deseje indicar uma casa vazia utilize a classe [Blank].)
 */
open class Tile protected constructor(val x: Int, val y: Int) {
  val position by lazy { Position(x,y) }
  open fun copy(x: Int = this.x, y: Int = this.y): Tile = Tile(x,y)
}

/**
 * Peça do tabuleiro que é controlada pelo jogador
 * Guarda as informações da ultima direção escolhida e quantos pontos o mesmo marcou durante a partida.
 */
class Player(
  x: Int,
  y: Int,
  val direction: Direction = Direction.LEFT, 
  val score: Int = 0,
  val tail: List<Position> = listOf(Position(x,y)),
  val life: Int = 1,
): Tile(x,y){
  
  fun copy(
    x: Int = this.x, y: Int = this.y,
    direction: Direction = this.direction,
    score: Int = this.score,
    tail: List<Position> = this.tail,  
    life: Int = this.life,
  ) = Player(x,y,direction, score,tail,life)
  
  fun copy(
    position: Position = this.position,
    direction: Direction = this.direction,
    score: Int = this.score,
    tail: List<Position> = this.tail,
    life: Int = this.life,
    ) = Player(position.x,position.y,direction, score,tail,life)
  
  override fun copy(
    x: Int,
    y: Int
  ) = Player(x,y,this.direction, this.score,this.tail,this.life)
}


/**
 * Acrescenta pontos ao jogador que conseguir chegar em uma casa desse tipo.
 */
class PlayerBody(
  x: Int,
  y: Int,
): Tile(x,y)

/**
 * Casa em branco no tabuleiro
 */
class Blank(
  x: Int,
  y: Int,
): Tile(x,y)

/**
 * Acrescenta pontos ao jogador que conseguir chegar em uma casa desse tipo.
 */
class Fruit(
  x: Int,
  y: Int,
): Tile(x,y)