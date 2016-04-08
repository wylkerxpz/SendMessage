
<?php
//ini_set('upload_max_filesize', '10M');
//ini_set('post_max_size', '10M');
//ini_set('max_input_time', 300);
//ini_set('max_execution_time', 300);

$target_path = "uploads/";

//Dados da mensagem
$sender = $_POST['sender'];
$receiver = $_POST['receiver'];
$arrayreceiver = explode(',', $receiver);
$subject = $_POST['subject'];
$mensage = $_POST['mensage'];

if($_FILES['midia']['name']) :
	$target_path = $target_path . basename($_FILES['midia']['name']);
	
	foreach ($arrayreceiver as $value) {
		$mens[] = array('sender' => $sender,
						'receiver' => $value,
						'subject' => $subject,
						'mensage' => $mensage,
						'arquivo' => basename($_FILES['midia']['name'])
					);
	}

	$mensagens = array('mensagens' => $mens);

	//converte o conteúdo do array para uma string JSON
	$json_str = json_encode($mensagens); 

	try {
	    //throw exception if can't move the file
	    if (!move_uploaded_file($_FILES['midia']['tmp_name'], $target_path)) {
	        throw new Exception('Não foi possivel mover o arquivo');
	    }

	    $fp = fopen('mensagens/'.date("Ymdhis").'.json', 'w');
		fwrite($fp, $json_str);
		fclose($fp);

	    echo "Mensagem enviada com anexo de midia";
	} catch (Exception $e) {
	    die('Erro arquivo não carregado: ' . $e->getMessage());
	}
else :
	foreach ($arrayreceiver as $value) {
		$mens[] = array('sender' => $sender,
						'receiver' => $value,
						'subject' => $subject,
						'mensage' => $mensage,
						'arquivo' => ''
					);
	}

	$mensagens = array('mensagens' => $mens);

	//converte o conteúdo do array para uma string JSON
	$json_str = json_encode($mensagens);

	$fp = fopen('mensagens/'.date("Ymdhis").'.json', 'w');
	fwrite($fp, $json_str);
	fclose($fp);

    echo "Mensagem enviada";
endif;



?>