export default async function getComputerMove(fenString) {
    try {
        const response = await fetch("http://localhost:8080/ChessEngine_war_exploded/engine", {
            method: "POST",
            body: JSON.stringify({"fen": fenString})
        })
        const data = await response.json()
        console.log(data)
        return data
    } catch (error) {
        console.log("Error getting computer move: ", error)
    }
    

}