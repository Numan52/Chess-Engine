import { isLoggedIn } from "./authentication";

document.getElementById('register-form').addEventListener('submit', async function(event) {
    event.preventDefault();
  
    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;
  
    
    if (password !== confirmPassword) {
      alert("Passwords do not match!");
      return;
    }

    try {
        const response = await fetch("http://localhost:8080/ChessEngine_war_exploded/register", {
          method : "POST",
          headers: { "Content-Type": "application/json"},
          body: JSON.stringify({
            username,
            email,
            password
          })
        })
  
        if (response.ok) {
          console.log("Successfull login")
          window.location.href = "/login.html"
          
        } else  {
          const message = await response.text()
          console.log(message)
        }
        
      } catch (error) {
        console.log(error)
      }
  
    
});


document.addEventListener("DOMContentLoaded", function() {
  if (isLoggedIn()) {
    window.location.href = "/"
  }
})
  