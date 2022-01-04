/** Converte Coordeanadas bidimensionais (x,y e largura do tabuleiro) em um indice para ser usado na lista de casas **/
fun xyToIndex(pos:Position, width:Int) = xyToIndex(pos.second,pos.first,width)
fun xyToIndex(x: Int,y: Int, width:Int) = (y * width) + x
/** Converte o indice e a largura do tabuleiro em um par de coordenadas bidimensionais referente ao indice **/
fun indexToXY(index: Int, width:Int) = Position(index/width,index % width)

/**
 *  Retorna o valor informado caso ele seja maior ou igual ao minimo, caso contrário retorna o 3º parâmetro 
 *  @param value valor à ser testado
 *  @param min valor minimo de teste
 *  @param fallback valor padrão caso o valor informado seja menor que o minimo
 *  @return clampToMin(5,10,25) => 25 ; clampToMin(5,0,25) => 5
 * **/
fun clampToMin(value: Int,min: Int, fallback: Int) = if (value >= min) value else fallback
/** atalho para [clampToMin] ([value],0,[fallback]) **/
fun clampTo0(value: Int, fallback: Int) = clampToMin(value, 0, fallback)
/**
 *  Retorna o valor informado caso ele seja menor ou igual ao máximo, caso contrário retorna o 3º parâmetro
 *  @param value valor à ser testado
 *  @param max valor máximo de teste
 *  @param fallback valor padrão caso o valor informado seja maior que o máximo
 *  @return clampToMax(11,10,10) => 10 ; clampToMax(10,10,3) => 10
 * **/
fun clampToMax(value: Int,max: Int, fallback: Int) = if (value <= max) value else fallback