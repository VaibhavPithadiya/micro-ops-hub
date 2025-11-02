document.addEventListener("DOMContentLoaded", () => {
  document.getElementById("fetchOrders").addEventListener("click", async () => {
    const userId = document.getElementById("userIdInput").value;
    if (!userId) return alert("Enter a valid user ID");
    const orders = await apiFetch(`${CONFIG.ORDER_SERVICE_BASE_URL}/orders/user/${userId}`);
    const tableBody = document.getElementById("ordersTableBody");
    tableBody.innerHTML = "";

    if (!orders) return;
    orders.forEach(o => {
      tableBody.innerHTML += `
        <tr>
          <td>${o.id}</td>
          <td>$${o.totalPrice}</td>
          <td>${new Date(o.createdAt).toLocaleString()}</td>
          <td>${o.items.map(i => `${i.productId}×${i.quantity}`).join(", ")}</td>
        </tr>`;
    });
  });
});
