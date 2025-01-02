import { isLoggedIn } from "./authentication";

document.getElementById('login-form').addEventListener('submit', async function(event) {
    event.preventDefault();
  
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
  
    try {
      const response = await fetch("http://localhost:8080/ChessEngine_war_exploded/login", {
        method : "POST",
        headers: { "Content-Type": "application/json"},
        body: JSON.stringify({
          username,
          password
        })
      })

    
      if (response.ok) {
        const json = await response.json()
        localStorage.setItem("token", json.token)
        console.log(json.message)
        window.location.href = "/index.html"
      } else {
        const message = await response.text()
        console.log(message)
      }
      
    } catch (error) {
      console.log(error)
    }
});

document.addEventListener("DOMContentLoaded", function() {
  if (isLoggedIn()) {
    console.log(isLoggedIn)
    window.location.href = "/"
  }
})