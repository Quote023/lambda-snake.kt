import kotlinx.browser.document
import org.w3c.dom.HTMLAudioElement
import org.w3c.dom.HTMLDivElement

fun updateScore(player: Player){
  val score = document.getElementById("score") as HTMLDivElement? ?: return
  (document.getElementById("game-audio-fx") as HTMLAudioElement?)?.run{
    src = "./on_point.wav"
    volume = 0.5
    play()
  }
  
  val pontos = player.score
  score.innerHTML = """
    |<p>Total de pontos: $pontos</p>
    """.trimMargin()
}

fun updateRanking(ranking: List<RankingEntry>){
  val scoreTable = document.getElementById("score-table") ?: return
  scoreTable.innerHTML = """
    <table>
      <tr>
        <th>Colocação</th>
        <th>Nome</th>
        <th>Pontuação</th>
      </tr>
      ${ ranking.mapIndexed { index,data ->  """
        <tr>
        <td>${index + 1}º</td>
        <td>${data.name}</td>
        <td>${data.score}</td>
      </tr>
      """.trimIndent()  }.joinToString("\n") }
    </table>
  """.trimIndent()
}
