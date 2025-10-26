async function apiFetch(url){
  try {
    const res = await fetch(url);
    if(!res.ok) throw new Error(await res.text());
    return res.json();
  } catch(e){
    console.error(e);
    alert("Error fetching data: "+e.message);
    return [];
  }
}