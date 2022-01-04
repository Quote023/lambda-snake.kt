import kotlinx.browser.document

fun currentScore(player: Player){
  val score = document.getElementById("score")
  if(score != null){
    val pontos = player.score
    score.innerHTML = """
      |<p>Total de pontos: $pontos</p>
      """.trimMargin()
  }
  
  val scoreTable = document.getElementById("score-table")
  if(scoreTable != null){
    scoreTable.innerHTML = """
      <table>
        <tr>
          <th>Colocação</th>
          <th>Pontuação</th>
        </tr>
        <tr>
          <td>1°</td>
          <td>321</td>
        </tr>
        <tr>
          <td>2°</td>
          <td>231</td>
        </tr>
        <tr>
          <td>3°</td>
          <td>123</td>
        </tr>
      </table>
    """.trimIndent()
  }
}
