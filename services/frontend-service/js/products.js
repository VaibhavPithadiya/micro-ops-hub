document.addEventListener("DOMContentLoaded", async () => {
  const products = await apiFetch(`${CONFIG.ORDER_SERVICE_BASE_URL}/products`);
  const tableBody = document.getElementById("productsTableBody");
  if (!products) return;

  products.forEach(p => {
    tableBody.innerHTML += `
      <tr>
        <td>${p.id}</td>
        <td>${p.name}</td>
        <td>${p.price}</td>
        <td>${p.stock}</td>
      </tr>`;
  });
});
