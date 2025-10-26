document.addEventListener("DOMContentLoaded", async () => {
  const users = await apiFetch(`${CONFIG.USERS_SERVICE}/users`);
  const tableBody = document.getElementById("usersTableBody");
  if (!users) return;

  users.forEach(u => {
    tableBody.innerHTML += `
      <tr>
        <td>${u.id}</td>
        <td>${u.name}</td>
        <td>${u.email}</td>
        <td>${u.role}</td>
      </tr>`;
  });
});
