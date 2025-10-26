// frontend/js/dashboard.js

let currentUser;

document.addEventListener("DOMContentLoaded", async () => {
  const user = JSON.parse(localStorage.getItem("user"));
  if (!user) return window.location.href = "../index.html";
  currentUser = user;
  document.getElementById("username").innerText = user.name;

  if (user.role === "ADMIN") document.getElementById("usersTab").style.display = "block";

  await updateStats();
  loadTab("dashboard");

  document.querySelectorAll(".tab-link").forEach(link => {
    link.addEventListener("click", (e) => {
      e.preventDefault();
      document.querySelectorAll(".tab-link").forEach(l => l.classList.remove("active"));
      link.classList.add("active");
      loadTab(link.dataset.tab);
    });
  });
});

async function updateStats() {
  const products = await apiFetch(`${CONFIG.ORDER_SERVICE_BASE_URL}/products`);
  const orders = await apiFetch(`${CONFIG.ORDER_SERVICE_BASE_URL}/orders/user/${currentUser.id}`);
  const users = currentUser.role === "ADMIN" ? await apiFetch(`${CONFIG.USER_SERVICE_BASE_URL}`) : [];
  document.getElementById("totalProducts").innerText = products.length;
  document.getElementById("totalOrders").innerText = orders.length;
  document.getElementById("totalUsers").innerText = users.length;
}

async function loadTab(tab) {
  const container = document.getElementById("tab-content");
  container.innerHTML = "<p>Loading...</p>";
  switch(tab){
    case "dashboard": container.innerHTML = "<h4>Welcome to MicroOps Hub Dashboard!</h4>"; break;
    case "products": loadProducts(container); break;
    case "orders": loadOrders(container); break;
    case "users": if(currentUser.role==="ADMIN") loadUsers(container); else container.innerHTML="<p>Access Denied</p>"; break;
  }
}

// -------------------- Products --------------------
async function loadProducts(container){
  const products = await apiFetch(`${CONFIG.ORDER_SERVICE_BASE_URL}/products`);
  container.innerHTML = `
    <div class="d-flex justify-content-between mb-3">
      <h4>Products</h4>
      ${currentUser.role==="ADMIN"?'<button class="btn btn-success" onclick="showProductModal()">Add Product</button>':''}
    </div>
    <div class="row g-3">
      ${products.map(p=>`
        <div class="col-md-4">
          <div class="card p-3 shadow-sm">
            <h5>${p.name}</h5>
            <p>${p.description||"No description"}</p>
            <p><strong>₹${p.price}</strong> | Stock: ${p.stock}</p>
            <button class="btn btn-primary" onclick="buyProduct(${p.id})">Buy</button>
            ${currentUser.role==="ADMIN"?`<button class="btn btn-sm btn-warning mt-2" onclick="editProduct(${p.id})">Edit</button>`:""}
          </div>
        </div>
      `).join('')}
    </div>
  `;
}

async function buyProduct(productId){
  const res = await fetch(`${CONFIG.ORDER_SERVICE_BASE_URL}/orders`, {
    method:"POST",
    headers:{"Content-Type":"application/json"},
    body:JSON.stringify({userId:currentUser.id, items:[{productId, quantity:1}]})
  });
  const data = await res.json();
  if(res.ok){ alert("Order placed successfully!"); updateStats(); } else alert(data.message || "Failed to buy product");
}

function showProductModal(){
  document.getElementById("productId").value="";
  document.getElementById("productName").value="";
  document.getElementById("productDesc").value="";
  document.getElementById("productPrice").value="";
  document.getElementById("productStock").value="";
  new bootstrap.Modal(document.getElementById("productModal")).show();
}

async function editProduct(id){
  const product = await apiFetch(`${CONFIG.ORDER_SERVICE_BASE_URL}/products`);
  const p = product.find(p=>p.id===id);
  document.getElementById("productId").value=p.id;
  document.getElementById("productName").value=p.name;
  document.getElementById("productDesc").value=p.description;
  document.getElementById("productPrice").value=p.price;
  document.getElementById("productStock").value=p.stock;
  new bootstrap.Modal(document.getElementById("productModal")).show();
}

async function saveProduct(){
  const id = document.getElementById("productId").value;
  const payload = {
    name: document.getElementById("productName").value,
    description: document.getElementById("productDesc").value,
    price: parseFloat(document.getElementById("productPrice").value),
    stock: parseInt(document.getElementById("productStock").value)
  };
  let url = `${CONFIG.ORDER_SERVICE_BASE_URL}/products`;
  let method = "POST";
  if(id){ url+=`/${id}`; method="PUT"; }
  const res = await fetch(url, {method, headers:{"Content-Type":"application/json"}, body:JSON.stringify(payload)});
  const data = await res.json();
  if(res.ok){ alert("Product saved!"); loadTab("products"); updateStats(); bootstrap.Modal.getInstance(document.getElementById("productModal")).hide(); }
  else alert(data.message||"Failed to save product");
}

// -------------------- Orders --------------------
async function loadOrders(container){
  const orders = await apiFetch(`${CONFIG.ORDER_SERVICE_BASE_URL}/orders/user/${currentUser.id}`);
  container.innerHTML = `
    <h4>My Orders</h4>
    <table class="table table-hover">
      <thead><tr><th>ID</th><th>Total</th><th>Created At</th><th>Items</th></tr></thead>
      <tbody>
        ${orders.map(o=>`<tr><td>${o.orderId}</td><td>₹${o.totalPrice}</td><td>${new Date(o.createdAt).toLocaleString()}</td><td>${o.items.map(i=>i.productId+"×"+i.quantity).join(", ")}</td></tr>`).join('')}
      </tbody>
    </table>
  `;
}

// -------------------- Users --------------------
async function loadUsers(container){
  const users = await apiFetch(`${CONFIG.USER_SERVICE_BASE_URL}`);
  container.innerHTML = `
    <h4>All Users</h4>
    <table class="table table-hover">
      <thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Role</th><th>Actions</th></tr></thead>
      <tbody>
        ${users.map(u=>`<tr><td>${u.id}</td><td>${u.name}</td><td>${u.email}</td><td>${u.role}</td><td><button class="btn btn-sm btn-primary" onclick="editUser(${u.id})">Edit</button></td></tr>`).join('')}
      </tbody>
    </table>
  `;
}

async function editUser(id){
  const users = await apiFetch(`${CONFIG.USER_SERVICE_BASE_URL}`);
  const u = users.find(u=>u.id===id);
  document.getElementById("userId").value=u.id;
  document.getElementById("userName").value=u.name;
  document.getElementById("userEmail").value=u.email;
  document.getElementById("userRole").value=u.role;
  document.getElementById("userPassword").value="dummy";
  new bootstrap.Modal(document.getElementById("userModal")).show();
}

async function saveUser(){
  const id = document.getElementById("userId").value;
  const payload = {
    name: document.getElementById("saveUserName").value,
    email: document.getElementById("saveUserEmail").value,
    password: document.getElementById("saveUserPassword").value,
    role: document.getElementById("saveUserRole").value
  };
  const res = await fetch(`${CONFIG.USER_SERVICE_BASE_URL}/${id}`, {method:"PUT", headers:{"Content-Type":"application/json"}, body:JSON.stringify(payload)});
  const data = await res.json();
  if(res.ok){ alert("User updated!"); loadTab("users"); bootstrap.Modal.getInstance(document.getElementById("userModal")).hide(); updateStats(); }
  else alert(data.message||"Failed to update user");
}

// -------------------- Utils --------------------
async function apiFetch(url){
  try{
    const res = await fetch(url);
    if(!res.ok) throw new Error(await res.text());
    return res.json();
  } catch(e){ console.error(e); alert("Error: "+e.message); return []; }
}

function logoutUser(){
  localStorage.removeItem("user");
  window.location.href="../index.html";
}
