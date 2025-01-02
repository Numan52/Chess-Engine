import { Chess } from "chess.js";
import getComputerMove from "./request.js";
import {
  addGameStartUi,
  initializeSurrenderButton,
  intializeTimeControl,
} from "./UICreator.js";
import { formatTime } from "./utils.js";
import { getUsernameFromToken, isLoggedIn } from "./authentication.js";

let board = null;
let game = new Chess();

let gameState = {
  gameId: 0,
  player: "w",
  timeLimit: 600, // seconds
  wRemainingSeconds: 0,
  bRemainingSeconds: 0,
  gameStarted: false,
  isTimeUp: false,

};


function updateLoginStatus() {

  const loginBtn = document.querySelector(".login-btn")
  const usernameSpan = document.querySelector(".username")
  const token = localStorage.getItem("token")

  if(isLoggedIn()) {
    const username = getUsernameFromToken(token)
    loginBtn.style.display = "none"
    usernameSpan.style.display = "inline"
    usernameSpan.textContent = username
  } else {
    loginBtn.style.display = "inline"
    usernameSpan.style.display = "none"
  }
  
  }


 
let startingPosition =
  "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
let $status = $("#status-message");

function onDragStart(source, piece, position, orientation) {
  // do not pick up pieces if the game is over
  if (game.isGameOver() || !gameState.gameStarted) return false;

  // only pick up pieces for the side to move
  if (
    (game.turn() === "w" && piece.search(/^b/) !== -1) ||
    (game.turn() === "b" && piece.search(/^w/) !== -1) ||
    game.turn() !== gameState.player
  ) {
    return false;
  }
}

function setPlayer(side) {
  let orientation;

  switch (side) {
    case "w":
      orientation = "white";
      break;
    case "b":
      orientation = "black";
      break;
    case "r":
      orientation = ["white", "black"].at(Math.round(Math.random()));
      break;
  }

  gameState.player = orientation[0];
  console.log("SIDE selected: ", side);
  board.orientation(orientation);
  console.log(game.turn());
}

function addResizehandler() {
  window.addEventListener("resize", () => {
    board.resize();
  });
}


function loginClickHandler() {
  const loginBtn = document.querySelector(".login-btn")
  loginBtn.addEventListener("click", () => {
    document.location.href = "/login.html"
  })
}

async function onDrop(source, target) {
  const statusDiv = document.querySelector(".status-message");

  try {
    makePlayerMove(source, target);
  } catch (error) {
    console.log("illegal move");
    return "snapback";
  }

  statusDiv.textContent =
    game.turn() === "w" ? "White to move" : "Black to move";

  // board.position(game.fen())
  try {
    await makeComputerMove();
  } catch (error) {
    console.log("An Error occurred: ", error);
    removeLoadingMsg();
    displayErrorMsg();
    return;
  }
  if (!gameState.gameStarted) {
    return
  }

  statusDiv.textContent =
    game.turn() === "w" ? "White to move" : "Black to move";
}

function makePlayerMove(source, target) {
  // see if the move is legal

  let move = game.move({
    from: source,
    to: target,
    promotion: "q", // NOTE: always promote to a queen for example simplicity
  });

  updateStatus();
  updateFenInfo(game.fen());
}

async function makeComputerMove() {
  displayLoadingMsg();
  // computer move
  let currentGameId = gameState.gameId

  const moveData = await getComputerMove(game.fen());

  removeErrorMsg();

  // ignore move if game over or new game started
  if (!gameState.gameStarted || currentGameId !== gameState.gameId) {
    return
  }

  console.log(moveData);
  
  game.move({
    from: moveData.from,
    to: moveData.to,
    promotion: "q",
  });
  

  removeLoadingMsg();
  updateStatus();
  board.position(game.fen());
  updateFenInfo(game.fen());
}

function displayLoadingMsg() {
  const loadingContainer = document.getElementById("loading-container");
  loadingContainer.classList.remove("hidden");
}

function displayErrorMsg() {
  const errorContainer = document.getElementById("error-msg");
  errorContainer.classList.remove("hidden");
}

function removeErrorMsg() {
  const errorContainer = document.getElementById("error-msg");
  errorContainer.classList.add("hidden");
}

function removeLoadingMsg() {
  const loadingContainer = document.getElementById("loading-container");
  loadingContainer.classList.add("hidden");
}

// update the board position after the piece snap
// for castling, en passant, pawn promotion
function onSnapEnd() {
  board.position(game.fen());
}

function updateStatus() {
  let status = "";

  let moveColor = "White";
  if (game.turn() === "b") {
    moveColor = "Black";
  }

  // checkmate?
  if (game.isCheckmate()) {
    status = "Game over, " + moveColor + " is in checkmate.";
  }

  // draw?
  else if (game.isDraw()) {
    status = "Game over, drawn position";
  }

  // game still on
  else {
    status = moveColor + " to move";

    // check?
    if (game.inCheck()) {
      status += ", " + moveColor + " is in check";
    }
  }

  $status.html(status);
}

function displayStartUI() {
  const sideContainer = document.querySelector(".choose-side-container");
  const minutesContainer = document.querySelector(".choose-minutes-container");

  addResizehandler();
  addGameStartUi(
    sideContainer,
    minutesContainer,
    setPlayer,
    startGame,
    setTime
  );

  updateFenInfo(game.fen());
}

function updateFenInfo(fen) {
  const info = document.querySelector(".info-bottom-tooltip-text");
  info.textContent = `Fen: ${game.fen()}`;
}

function setTime(minutes) {
  // wRemainingSeconds = minutes * 60
  gameState.timeLimit = minutes * 60
  gameState.wRemainingSeconds = minutes * 60;
  gameState.bRemainingSeconds = minutes * 60;
}

function updateTime(ownTimeSelector, oppTimeSelector) {
  const ownTime = document.querySelector(ownTimeSelector);

  const oppTime = document.querySelector(oppTimeSelector);
  const statusDiv = document.querySelector(".status-message");

  const timerInterval = setInterval(() => {
    if (game.turn() === "w") {
      gameState.wRemainingSeconds--;
      if (gameState.player === "w") {
        ownTime.textContent = formatTime(gameState.wRemainingSeconds);
      } else {
        oppTime.textContent = formatTime(gameState.wRemainingSeconds);
      }
      
      if (gameState.wRemainingSeconds === 0 || !gameState.gameStarted) {
        handleGameOver(true, statusDiv);
        clearInterval(timerInterval);
      }
    } else {
      gameState.bRemainingSeconds--;
      if (gameState.player === "b") {
        ownTime.textContent = formatTime(gameState.bRemainingSeconds);
      } else {
        oppTime.textContent = formatTime(gameState.bRemainingSeconds);
      }
      if (gameState.bRemainingSeconds === 0 || !gameState.gameStarted) {
        handleGameOver(false, statusDiv);
        clearInterval(timerInterval);
      }
    }

  }, 1000);
}

function handlePlayAgain(playAgainBtn) {
  const timeControlContainer = document.querySelector(
    ".time-control-container"
  );
  const gameStartContainer = document.querySelector(".game-start-container");

  resetBoard();
  playAgainBtn.classList.add("hidden");
  timeControlContainer.classList.add("hidden");
  gameStartContainer.classList.remove("hidden");
}

function handleGameOver(isWhite, statusDiv) {
  const cancelGameButton = document.querySelector(".surrender-btn");
  const playAgainButton = document.querySelector(".play-again-button");
  removeLoadingMsg()
  gameState.isTimeUp =
    gameState.wRemainingSeconds === 0 || gameState.bRemainingSeconds === 0;

  gameState.gameStarted = false;
  if (gameState.isTimeUp) {
    if (isWhite) {
      statusDiv.textContent = "Time's up. Black wins.";
    } else {
      statusDiv.textContent = "Time's up. White wins.";
    }
  }

  cancelGameButton.classList.add("hidden");

  playAgainButton.classList.remove("hidden");
  gameState.wRemainingSeconds = gameState.timeLimit
  gameState.bRemainingSeconds = gameState.timeLimit

  playAgainButton.addEventListener("click", () =>
    handlePlayAgain(playAgainButton)
  );
}

function resetBoard() {
  board.start();
  game.reset();
  updateFenInfo();
}

function startGame() {
  const startGameContainer = document.querySelector(".game-start-container");
  const timeControlContainer = document.querySelector(
    ".time-control-container"
  );
  const statusMsg = document.querySelector(".status-message");
  const surrenderButton = document.querySelector(".surrender-btn");
  const ownTimeSelector = ".own-remaining-time";
  const oppTimeSelector = ".opp-remaining-time";

  gameState.gameId++

  const color = gameState.player === "w" ? "white" : "black";

  if (gameState.player === "b") {
    makeComputerMove();
  }

  intializeTimeControl(gameState.timeLimit);
  timeControlContainer.classList.remove("hidden");

  startGameContainer.classList.add("hidden");

  let isStartingPosition = game.fen() === startingPosition;
  initializeSurrenderButton(surrenderButton, isStartingPosition, () => {
    gameState.gameStarted = false;
    if (isStartingPosition) {
      statusMsg.textContent = `Game canceled`;
    } else {
      let engineColor = gameState.player === "w" ? "Black" : "White";
      statusMsg.textContent = `You surrendered. ${engineColor} won.`;
    }
  });

  updateTime(ownTimeSelector, oppTimeSelector);
  gameState.gameStarted = true;
}

let fen = "rnbqkbnr/ppp1p1pp/5p2/3pN3/4P3/8/PPPP1PPP/RNBQKB1R b KQkq - 1 3";

var config = {
  draggable: true,
  position: "start",
  onDragStart: onDragStart,
  onDrop: onDrop,
  onSnapEnd: onSnapEnd,
};

board = Chessboard("board", config);

displayStartUI();
console.log(JSON.stringify({ fen: "dasdadad asdad asdada" }));
loginClickHandler()
updateLoginStatus()
updateStatus();
