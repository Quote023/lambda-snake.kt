/**
 * @typedef {{
 *  path: string,
 *  httpMethod: string,
 *  headers: Record<string,
 *  string>,
 *  queryStringParameters: Record<string,
 *  string>,
 *  body: string,
 *  isBase64Encoded: boolean
 * }} NetlifyEvent
 */

/**
 * @typedef {{
 *   isBase64Encoded?: boolean,
 *   statusCode: 200 | 400 | 500 | 300,
 *   headers?: Record<string,string>,
 *   multiValueHeaders?: Record<string, string[]>,
 *   body: string
 * }} NetlifyReturn
 */

const fetch = require('node-fetch')


/**
 * 
 * @param {NetlifyEvent} event
 * @param context
 * @returns {Promise<NetlifyReturn>}
 */
async function updateScore(event, context) {
  const userData = JSON.parse(event.body)
  
  //Validation
  const body = {
    name: userData.name || throw new Error("Missign name"),
    score: userData.score || throw new Error("Missign score")
  }
  
  await fetch('https://jsonbin.org/me/leaderboard', {
    headers: { authorization: `token ${process.env.JSON_KEY}`},
    method: "PATCH",
    body: JSON.stringify(body)
  }).catch(() => undefined)
  
  return {
    statusCode: 200,
    body: JSON.stringify({status: "success"})
  }
}

exports.handler = updateScore