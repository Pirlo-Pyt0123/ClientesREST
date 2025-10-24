let jwtToken = '';

function login() {
  fetch('http://localhost:8000/api/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      email: document.getElementById('email').value,
      password: document.getElementById('password').value
    })
  })
  .then(r => r.json())
  .then(data => {
    jwtToken = data.token;
    document.getElementById('token').textContent = 'Token: ' + jwtToken;
  });
}

function getPersonas() {
  fetch('http://localhost:8000/api/personas', {
    headers: {
      'Authorization': 'Bearer ' + jwtToken
    }
  })
  .then(r => r.json())
  .then(data => {
    let html = "<table border='1'><tr><th>ID</th><th>Nombres</th><th>Apellidos</th><th>CI</th><th>Dirección</th><th>Teléfono</th><th>Email</th><th>Acciones</th></tr>";
    data.forEach(p => {
      html += `<tr>
        <td>${p.id}</td>
        <td>${p.nombres}</td>
        <td>${p.apellidos}</td>
        <td>${p.ci}</td>
        <td>${p.direccion}</td>
        <td>${p.telefono}</td>
        <td>${p.email}</td>
        <td>
          <button onclick="editarPersona(${p.id}, '${p.nombres}', '${p.apellidos}', '${p.ci}', '${p.direccion}', '${p.telefono}', '${p.email}')">Editar</button>
          <button onclick="eliminarPersona(${p.id})">Eliminar</button>
        </td>
      </tr>`;
    });
    html += "</table>";
    document.getElementById('personas').innerHTML = html;
  });
}

function crearPersona() {
  fetch('http://localhost:8000/api/personas', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + jwtToken
    },
    body: JSON.stringify({
      nombres: document.getElementById('nombres').value,
      apellidos: document.getElementById('apellidos').value,
      ci: document.getElementById('ci').value,
      direccion: document.getElementById('direccion').value,
      telefono: document.getElementById('telefono').value,
      email: document.getElementById('emailPersona').value
    })
  })
  .then(r => r.json())
  .then(data => {
    document.getElementById('resultadoPOST').textContent = JSON.stringify(data, null, 2);
    getPersonas(); // refresca la lista
  });
}

function editarPersona(id, nombres, apellidos, ci, direccion, telefono, email) {
  // Carga los datos al formulario para editar
  document.getElementById('nombres').value = nombres;
  document.getElementById('apellidos').value = apellidos;
  document.getElementById('ci').value = ci;
  document.getElementById('direccion').value = direccion;
  document.getElementById('telefono').value = telefono;
  document.getElementById('emailPersona').value = email;
  // Cambia el botón de crear a actualizar
  document.querySelector('button[onclick="crearPersona()"]').style.display = "none";
  if(!document.getElementById('btnActualizar')) {
    let btn = document.createElement('button');
    btn.textContent = "Actualizar Persona";
    btn.id = "btnActualizar";
    btn.onclick = function() {
      actualizarPersona(id);
    };
    document.getElementById('resultadoPOST').insertAdjacentElement('afterend', btn);
  }
}

function actualizarPersona(id) {
  fetch('http://localhost:8000/api/personas/' + id, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + jwtToken
    },
    body: JSON.stringify({
      nombres: document.getElementById('nombres').value,
      apellidos: document.getElementById('apellidos').value,
      ci: document.getElementById('ci').value,
      direccion: document.getElementById('direccion').value,
      telefono: document.getElementById('telefono').value,
      email: document.getElementById('emailPersona').value
    })
  })
  .then(r => r.json())
  .then(data => {
    document.getElementById('resultadoPOST').textContent = "Actualizado correctamente";
    getPersonas();
    document.querySelector('button[onclick="crearPersona()"]').style.display = "inline";
    let btnActualizar = document.getElementById('btnActualizar');
    if(btnActualizar) btnActualizar.remove();
    // Limpia el formulario
    document.getElementById('nombres').value = "";
    document.getElementById('apellidos').value = "";
    document.getElementById('ci').value = "";
    document.getElementById('direccion').value = "";
    document.getElementById('telefono').value = "";
    document.getElementById('emailPersona').value = "";
  });
}

function eliminarPersona(id) {
  fetch('http://localhost:8000/api/personas/' + id, {
    method: 'DELETE',
    headers: {
      'Authorization': 'Bearer ' + jwtToken
    }
  })
  .then(r => {
    if(r.status === 204) {
      document.getElementById('resultadoPOST').textContent = "Persona eliminada.";
      getPersonas();
    } else {
      r.json().then(data => document.getElementById('resultadoPOST').textContent = "Error al eliminar.");
    }
  });
}
