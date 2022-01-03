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


/*
As classes aqui funcionam como uma simulação de tipo de dados algebraicos/compostos
*/

/**
 * Classe base de todas as casas do Tabuleiro
 * construtor marcado como protected pois em situações reais essa classe base nunca deve ser usada
 * (Caso deseje indicar uma casa vazia utilize a classe [Blank].)
 */
open class Tile protected constructor(val x: Int, val y: Int) {
  val position by lazy { Pair(x,y) }
  open fun copy(x: Int = this.x, y: Int = this.y): Tile = Tile(x,y)
}

/**
 * Peça do tabuleiro que é controlada pelo jogador
 * Guarda as informações da ultima direção escolhida e quantos pontos o mesmo marcou durante a partida.
 */
class Player(
  x: Int,
  y: Int,
  val direction: Direction = Direction.LEFT
): Tile(x,y){
  fun copy(x: Int = this.x, y: Int = this.y, direction: Direction = this.direction) = Player(x,y,direction)
  override fun copy(x: Int, y: Int) = Player(x,y,this.direction)
}

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