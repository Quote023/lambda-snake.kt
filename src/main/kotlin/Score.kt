import kotlinx.browser.document
import org.w3c.dom.HTMLAudioElement
import org.w3c.dom.HTMLParagraphElement

fun updateScore(player: Player){
  val score = document.getElementById("score-text") as HTMLParagraphElement? ?: return
  
  if(player.score > 0) {
    (document.getElementById("game-audio-fx") as HTMLAudioElement?)?.run {
      src = "./on_point.wav"
      volume = 0.2
      play()
    }
  }
  
  val pontos = player.score
  score.innerText = "Total de pontos: $pontos"
}

fun updateRanking(ranking: List<RankingEntry>){
  val scoreTable = document.getElementById("score-table") ?: return
  scoreTable.innerHTML = """
    <table class="score-table-el">
      <tr class="score-tr">
        <th class="score-th">Colocação</th>
        <th class="score-th">Nome</th>
        <th class="score-th">Pontuação</th>
      </tr>
      ${ ranking.mapIndexed { index,data ->  """
        <tr class="score-tr">
        <td class="score-td">${index + 1}º</td>
        <td class="score-td">${data.name}</td>
        <td class="score-td">${data.score}</td>
      </tr>
      """.trimIndent()  }.joinToString("\n") }
    </table>
  """.trimIndent()
}
