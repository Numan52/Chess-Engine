import { Chess } from "chess.js"
import getComputerMove from "./request.js"

let board = null
let game = new Chess
const player = "w"
let $status = $('#status')
let $fen = $('#fen')
let $pgn = $('#pgn')

function onDragStart (source, piece, position, orientation) {
  // do not pick up pieces if the game is over
  if (game.isGameOver()) return false

  // only pick up pieces for the side to move
  if ((game.turn() === 'w' && piece.search(/^b/) !== -1) ||
      (game.turn() === 'b' && piece.search(/^w/) !== -1) ||
      (game.turn() !== player)) {
    return false
  }

}

async function onDrop (source, target) {
  // see if the move is legal
  
  try {
    let move = game.move({
      from: source,
      to: target,
      promotion: 'q' // NOTE: always promote to a queen for example simplicity
    })
  } catch (error) {
    console.log("illegal move")
    return 'snapback'
  }
  updateStatus()
  // board.position(game.fen())


  displayLoadingMsg()
  // computer move
  const moveData = await getComputerMove(game.fen())
  console.log(moveData)

  game.move({
    from: moveData.from,
    to: moveData.to,
    promotion: 'q'
  })

  removeLoadingMsg()
  updateStatus()
  board.position(game.fen())
}


function displayLoadingMsg() {
  const loadingContainer = document.getElementById("loading-container")
  loadingContainer.classList.remove("hidden")
}


function removeLoadingMsg() {
  const loadingContainer = document.getElementById("loading-container")
  loadingContainer.classList.add("hidden")
}


// update the board position after the piece snap
// for castling, en passant, pawn promotion
function onSnapEnd () {
  board.position(game.fen())
}

function updateStatus () {
  let status = ''

  let moveColor = 'White'
  if (game.turn() === 'b') {
    moveColor = 'Black'
  }

  // checkmate?
  if (game.isCheckmate()) {
    status = 'Game over, ' + moveColor + ' is in checkmate.'
  }

  // draw?
  else if (game.isDraw()) {
    status = 'Game over, drawn position'
  }

  // game still on
  else {
    status = moveColor + ' to move'

    // check?
    if (game.inCheck()) {
      status += ', ' + moveColor + ' is in check'
    }
  }

  $status.html(status)
  $fen.html(game.fen())
  $pgn.html(game.pgn())
}

let fen = 'rnbqkbnr/ppp1p1pp/5p2/3pN3/4P3/8/PPPP1PPP/RNBQKB1R b KQkq - 1 3'

var config = {
  draggable: true,
  position: "start",
  onDragStart: onDragStart,
  onDrop: onDrop,
  onSnapEnd: onSnapEnd
}


console.log("hello")
board = Chessboard('board', config)

console.log(JSON.stringify({ fen: "dasdadad asdad asdada"}))

updateStatus()