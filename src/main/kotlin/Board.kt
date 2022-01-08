@file:Suppress("unused")

import org.w3c.dom.CanvasRenderingContext2D
import kotlin.random.Random


fun move(position: Position, direction: Direction, topLimit: Int, bottomLimit: Int): Position = when(direction){
  Direction.UP    -> Position(position.x, clampTo0(position.y - 1, topLimit))
  Direction.DOWN  -> Position(position.x, clampToMax(position.y + 1, topLimit,0))
  Direction.LEFT  -> Position(clampTo0(position.x - 1, bottomLimit), position.y)
  Direction.RIGHT -> Position(clampToMax(position.x + 1, bottomLimit,0), position.y)
}

/**
 * Calcula para qual posição o [player] deve se mover baseado na sua propriedade **player.direction**
 * Caso a posição calculada passe dos limites do tabuleiro, o player aparece na direção oposta da tela.
 */
fun movePlayer(board: Board, player: Player): Board {
  val nextPosition = move(player.position,player.direction,board.height - 1,board.width - 1)
  val targetTile = tileAt(board,nextPosition)
  
  val newPlayer = when(targetTile){
    is Fruit -> player.copy(score = player.score + 1,position = nextPosition,tail =  listOf(nextPosition) + player.tail)
    is PlayerBody -> player.copy(life = player.life - 1)
    else -> player.copy(position = nextPosition, tail = listOf(nextPosition) + player.tail.dropLast(1))
  }
  
  val newFruitPos = if(targetTile is Fruit) randomValidPosition(board) else null
  
  val newTiles = board.map {
    when(it.position){
      newPlayer.position     -> newPlayer //Nova Cabeça
      in newPlayer.tail      -> PlayerBody(it.x,it.y) //Rastro do Player
      player.tail.lastOrNull()     -> Blank(it.x, it.y) //Limpa o rastro atrás
      newFruitPos -> Fruit(it.x,it.y)
      else -> it
    }
  }
  
  return board.copy(tiles=newTiles, player = newPlayer)
}

fun tileAt(board: Board,position: Position): Tile {
  return tileAt(board,xyToIndex(position, board.width))
}
fun tileAt(board: Board,index: Int): Tile {
  return board[index]
}

/**
 * Move a casa [movedTile] para uma nova [posição desejada][newPos]
 * Define a posição original de [movedTile] como uma casa vazia
 * E cria uma cópia de [movedTile] na posição [newPos]
 */
fun moveTile(board: Board, movedTile: Tile, newPos: Position): Board {
  return board.copy(tiles=board.map {
    when(it.position){
      newPos -> movedTile.copy(it.x,it.y)
      movedTile.position -> Blank(it.x,it.y)
      else -> it
    }
  })
}

/**
 * Posiciona a casa [target] na posição definida pela sua propriedade Position, remove a casa que estiver no lugar ignorando seus efeitos
 */
fun replaceTile(board: Board, target: Tile): Board {
  return board.copy(tiles = board.map {
    when(it.position){
      target.position -> target
      else -> it
    }
  })
}


/**
 * Imprime no Canvas cada casa com sua respectiva cor
 */
fun renderBoard(context: CanvasRenderingContext2D, board:Board){
  //TODO: transformar esse for em um map ou fold
  //Percorre todas as casas do tabuleiro e desenha uma por uma na tela.
  (0 until (board.width * board.height)).forEach{ index -> 
      val pos = indexToXY(index,board.width)
      val tile = board[index]

      //Define a cor do próximo quadrado a ser desenhado baseado em qual peça será impressa.
      context.fillStyle = when(tile) {
        is Player -> {
          "green"
        }
        is PlayerBody -> {
          "blue"
        }
        is Fruit -> {
          "red"
        }
        else -> {
          "white"
        }
      }
      
      //Desenha um quadrado de 10x10 pixeis representando 1 casa do tabuleiro
      //o (x,y) é multiplicado por 10 pois o canvas está escalado em 10x (temos um tabuleiro de 30x30 em um canvas de 300x300)
      context.fillRect(pos.x.toDouble() * 10, pos.y.toDouble() * 10, 10.0,10.0)
  }
}


fun randomValidPosition(board:Board): Position {
  val result = randomPosition(board)
  return if(tileAt(board,result) is Blank) 
    result 
  else 
    randomValidPosition(board)
}

fun randomPosition(board: Board): Position = randomPosition(board.width,board.height)
fun randomPosition(maxWidth: Int, maxHeight: Int): Position {
  val rng = Random.Default
  return Position(rng.nextInt(maxWidth), rng.nextInt(maxHeight))
}

/**
 * Gera um tabuleiro aleatório no tamanho especificado com 1 player e 1 fruta
 */
fun initRandomBoard(width: Int, height: Int): Board {
  val playerPos = randomPosition(width,height)
  val fruitPos = randomPosition(width,height)

  //Caso os valores calculados sejam iguais, recalcula em uma nova posição até termos posições diferentes.
  if(playerPos == fruitPos) return initRandomBoard(width,height)

  val tiles = (0 until (width * height)).map {  pos ->
    //Percorremos aqui todos os x/y possiveis
    //E instânciamos Players e Frutas em suas respectivas posições calculadas, todas as demais posições serão casas em branco.
    with(indexToXY(pos,width)) {
      when (this) {
        playerPos -> Player(this.x,this.y)
        fruitPos -> Fruit(this.x,this.y)
        else -> Blank(this.x,this.y)
      }
    }
  }
  
  return Board(width,height,tiles)
}

