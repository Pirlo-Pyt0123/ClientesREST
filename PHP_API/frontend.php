<?php
session_start();

$base_url = "http://localhost:8000/api";

function apiRequest($url, $method = 'GET', $data = null, $token = null) {
    global $base_url;
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($ch, CURLOPT_CUSTOMREQUEST, $method);

    $headers = ['Content-Type: application/json'];
    if($token) $headers[] = 'Authorization: Bearer ' . $token;
    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);

    if($data) {
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
    }
    $result = curl_exec($ch);
    curl_close($ch);
    return $result;
}

// LOGIN
if(isset($_POST['email']) && isset($_POST['password'])) {
    $data = [
        "email" => $_POST['email'],
        "password" => $_POST['password']
    ];
    $resp = json_decode(apiRequest($base_url . "/login", "POST", $data));
    if(isset($resp->token)) {
        $_SESSION['jwt'] = $resp->token;
        $_SESSION['mensaje'] = "Login OK.";
    } else {
        $_SESSION['mensaje'] = "Login incorrecto.";
    }
    header("Location: ".$_SERVER['PHP_SELF']);
    exit();
}

// CREAR PERSONA
if(isset($_POST['crear'])) {
    $data = [
        "nombres" => $_POST['nombres'],
        "apellidos" => $_POST['apellidos'],
        "ci" => $_POST['ci'],
        "direccion" => $_POST['direccion'],
        "telefono" => $_POST['telefono'],
        "email" => $_POST['emailPersona']
    ];
    $resp = apiRequest($base_url . "/personas", "POST", $data, $_SESSION['jwt'] ?? null);
    $_SESSION['mensaje'] = "Respuesta crear: " . $resp;
    header("Location: ".$_SERVER['PHP_SELF']);
    exit();
}

// ELIMINAR PERSONA
if(isset($_GET['eliminar'])) {
    $id = $_GET['eliminar'];
    $resp = apiRequest($base_url . "/personas/$id", "DELETE", null, $_SESSION['jwt'] ?? null);
    $_SESSION['mensaje'] = "Respuesta eliminar: " . $resp;
    header("Location: ".$_SERVER['PHP_SELF']);
    exit();
}

// EDITAR PERSONA (mostrar formulario)
$edit_persona = null;
if(isset($_GET['editar'])) {
    $id = $_GET['editar'];
    $resp = apiRequest($base_url . "/personas/$id", "GET", null, $_SESSION['jwt'] ?? null); 
    $edit_persona = json_decode($resp);
}

// GUARDAR EDICIÓN
if(isset($_POST['actualizar'])) {
    $id = $_POST['id'];
    $data = [
        "nombres" => $_POST['nombres'],
        "apellidos" => $_POST['apellidos'],
        "ci" => $_POST['ci'],
        "direccion" => $_POST['direccion'],
        "telefono" => $_POST['telefono'],
        "email" => $_POST['emailPersona']
    ];
    $resp = apiRequest($base_url . "/personas/$id", "PUT", $data, $_SESSION['jwt'] ?? null);
    $_SESSION['mensaje'] = "Respuesta actualizar: " . $resp;
    header("Location: ".$_SERVER['PHP_SELF']);
    exit();
}

// LISTAR PERSONAS
$listado = [];
if(isset($_SESSION['jwt'])) {
    $resp = apiRequest($base_url . "/personas", "GET", null, $_SESSION['jwt']);
    $listado = json_decode($resp, true) ?? [];
}
?>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Frontend PHP Personas</title>
</head>
<body>
    <?php if(isset($_SESSION['mensaje'])): ?>
        <div><b><?php echo $_SESSION['mensaje']; unset($_SESSION['mensaje']);?></b></div>
    <?php endif; ?>

    <?php if(!isset($_SESSION['jwt'])): ?>
        <h3>Login</h3>
        <form method="post">
            <input name="email" placeholder="Email">
            <input name="password" placeholder="Password" type="password">
            <button type="submit">Iniciar sesión</button>
        </form>
    <?php else: ?>
        <h3>Crear Persona</h3>
        <form method="post">
            <input name="nombres" placeholder="Nombres" value="<?php echo $edit_persona->nombres ?? '' ?>">
            <input name="apellidos" placeholder="Apellidos" value="<?php echo $edit_persona->apellidos ?? '' ?>">
            <input name="ci" placeholder="CI" value="<?php echo $edit_persona->ci ?? '' ?>">
            <input name="direccion" placeholder="Dirección" value="<?php echo $edit_persona->direccion ?? '' ?>">
            <input name="telefono" placeholder="Teléfono" value="<?php echo $edit_persona->telefono ?? '' ?>">
            <input name="emailPersona" placeholder="Email" value="<?php echo $edit_persona->email ?? '' ?>">
            <?php if($edit_persona): ?>
                <input type="hidden" name="id" value="<?php echo $edit_persona->id; ?>">
                <button type="submit" name="actualizar">Actualizar Persona</button>
            <?php else: ?>
                <button type="submit" name="crear">Crear Persona</button>
            <?php endif;?>
        </form>
        <hr>
        <h3>Listado de personas</h3>
        <table border="1">
            <tr>
                <th>ID</th><th>Nombres</th><th>Apellidos</th><th>CI</th><th>Dirección</th><th>Teléfono</th><th>Email</th><th>Acciones</th>
            </tr>
            <?php foreach($listado as $p): ?>
                <tr>
                    <td><?php echo $p['id'];?></td>
                    <td><?php echo $p['nombres'];?></td>
                    <td><?php echo $p['apellidos'];?></td>
                    <td><?php echo $p['ci'];?></td>
                    <td><?php echo $p['direccion'];?></td>
                    <td><?php echo $p['telefono'];?></td>
                    <td><?php echo $p['email'];?></td>
                    <td>
                        <a href="?editar=<?php echo $p['id'];?>">Editar</a>
                        <a href="?eliminar=<?php echo $p['id'];?>" onclick="return confirm('¿Borrar persona?');">Eliminar</a>
                    </td>
                </tr>
            <?php endforeach;?>
        </table>
        <hr>
        <form method="post" action="logout.php">
            <button type="submit">Cerrar sesión</button>
        </form>
    <?php endif; ?>
</body>
</html>
