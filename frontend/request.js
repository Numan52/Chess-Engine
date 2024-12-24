export default async function getComputerMove(fenString) {
    
    const response = await fetch(`${import.meta.env.VITE_API_URL}/engine`, {
        method: "POST",
        body: JSON.stringify({"fen": fenString})
    })
    const data = await response.json()
    console.log(data)
    return data


}