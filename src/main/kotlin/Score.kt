import kotlinx.browser.document

fun currentScore(player: Player){
  val score = document.getElementById("score")
  if(score != null){
    val pontos = player.score
    score.innerHTML = """"
      |<h1>Total de pontos: $pontos</h1>
      |
      """.trimMargin()

  }
}