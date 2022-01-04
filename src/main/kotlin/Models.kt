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

data class Board(
  val width: Int,
  val height: Int,
  val tiles: List<Tile> = listOf<Tile>(),
): List<Tile> by tiles {
  
}

typealias Position = Pair<Int,Int>

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
  val tail: List<Position> = listOf(Position(x,y))
): Tile(x,y){
  
  fun copy(
    x: Int = this.x, y: Int = this.y,
    direction: Direction = this.direction,
    score: Int = this.score,
    tail: List<Position> = this.tail  
  ) = Player(x,y,direction, score,tail)
  
  fun copy(
    position: Position = this.position,
    direction: Direction = this.direction,
    score: Int = this.score,
    tail: List<Position> = this.tail
  ) = Player(position.first,position.second,direction, score,tail)
  
  override fun copy(
    x: Int,
    y: Int
  ) = Player(x,y,this.direction, this.score,this.tail)
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