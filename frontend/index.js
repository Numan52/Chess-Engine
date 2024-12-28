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


function addResizehandler() {
  window.addEventListener("resize", (event) => {
    board.resize()
  })
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
  updateFenInfo(game.fen())
  // board.position(game.fen())


  displayLoadingMsg()
  // computer move
  try {
    const moveData = await getComputerMove(game.fen())

    removeErrorMsg()

    console.log(moveData)
    
    game.move({
      from: moveData.from,
      to: moveData.to,
      promotion: 'q'
    })
    console.log("computer")
    removeLoadingMsg()
    updateStatus()
    board.position(game.fen())
    updateFenInfo(game.fen())

  } catch (error) {
    console.log("An Error occurred.")
    removeLoadingMsg()
    displayErrorMsg()
    return
  }
  
  
}


function displayLoadingMsg() {
  const loadingContainer = document.getElementById("loading-container")
  loadingContainer.classList.remove("hidden")
}


function displayErrorMsg() {
  const errorContainer = document.getElementById("error-msg")
  errorContainer.classList.remove("hidden")
}

function removeErrorMsg() {
  const errorContainer = document.getElementById("error-msg")
  errorContainer.classList.add("hidden")
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


function intialize() {
  addResizehandler()

  updateFenInfo(game.fen())
}


function updateFenInfo(fen) {
  const info = document.querySelector(".info-bottom-tooltip-text")
  info.textContent = `Fen: ${game.fen()}`
}


console.log("hello")
board = Chessboard('board', config)

intialize()
console.log(JSON.stringify({ fen: "dasdadad asdad asdada"}))

updateStatus()