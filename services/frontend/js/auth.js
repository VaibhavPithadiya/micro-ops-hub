// frontend/js/auth.js

async function registerUser(event) {
  event.preventDefault();
  const name = document.getElementById("name").value.trim();
  const email = document.getElementById("email").value.trim();
  const password = document.getElementById("password").value.trim();

  const res = await fetch(`${CONFIG.USER_SERVICE_BASE_URL}/users/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name, email, password }),
  });

  const data = await res.json();

  if (res.ok) {
    alert("Registration successful! Please log in.");
    window.location.href = "login.html";
  } else {
    alert(data.message || "Registration failed.");
  }
}

async function loginUser(event) {
  event.preventDefault();
  const email = document.getElementById("email").value.trim();
  const password = document.getElementById("password").value.trim();

  const res = await fetch(`${CONFIG.USER_SERVICE_BASE_URL}/users/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  });

  const data = await res.json();

  if (res.ok) {
    localStorage.setItem("user", JSON.stringify(data));
    window.location.href = "dashboard.html";
  } else {
    alert(data.message || "Invalid credentials.");
  }
}

function logoutUser() {
  localStorage.removeItem("user");
  window.location.href = "../index.html";
}
