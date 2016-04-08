<?php
    $con = mysql_connect("mysql.consertar.srv.br","wylker","Pbge6nsp@");
    $db= mysql_select_db("dsdm");
    
    $username = $_POST["username"];
    $password = $_POST["password"];

    if (!empty($_POST)) {
        if (empty($_POST['username']) || empty($_POST['password'])) {
            // Create some data that will be the JSON response 
            $response["success"] = 0;
            $response["message"] = " Um ou mais campos estao vazios ";

            //die is used to kill the page, will not let the code below to be executed. It will also
            //display the parameter, that is the json data which our android application will parse to be //shown to the users
            die(json_encode($response));
        }

        $query = " SELECT * FROM Docente WHERE docente_usuario = '$username' and docente_senha = '$password'";

        // executa a query
        $dados = mysql_query($query, $con) or die(mysql_error());
        // transforma os dados em um array
        $linha = mysql_fetch_assoc($dados);
        // calcula quantos dados retornaram
        $total = mysql_num_rows($dados);

        // Verifica se existe um usuario
        if ($total == 1) {
            //$user["docente_nome"] = $linha['docente_nome'];
            $docente_pk = $linha['docente_pk'];
            $docente_nome = $linha['docente_nome'];

            $query = "SELECT * FROM Coordenacao WHERE docente_pk = '$docente_pk'";

            // executa a query
            $dados = mysql_query($query, $con) or die(mysql_error());
            // transforma os dados em um array
            $linha = mysql_fetch_assoc($dados);
            // calcula quantos dados retornaram
            $total = mysql_num_rows($dados);

            if($total == 1) {
                $coordenacao_pk = $linha['coordenacao_pk'];
                $query = "SELECT * FROM Disciplina WHERE coordenacao_pk = '$coordenacao_pk'";

                // executa a query
                $dados = mysql_query($query, $con) or die(mysql_error());
                // transforma os dados em um array
                $linha = mysql_fetch_assoc($dados);
                // calcula quantos dados retornaram
                $total = mysql_num_rows($dados);

                do { 
                    $disciplinas[] = array('disciplina_pk' => $linha['disciplina_pk'],
                                           'disciplina_nome' => $linha['disciplina_nome'],
                                           'selected' => false
                                    );
                // finaliza o loop que vai mostrar os dados
                }while($linha = mysql_fetch_assoc($dados));
                $response = array('docente_pk' => $docente_pk, 'docente_nome' => $docente_nome, 'info' => array('disciplinas' => $disciplinas));
                die(json_encode($response));

            } else {
                $query = "SELECT disciplina_pk FROM Ministra WHERE docente_pk = '$docente_pk'";

                // executa a query
                $dados = mysql_query($query, $con) or die(mysql_error());
                // transforma os dados em um array
                $linha = mysql_fetch_assoc($dados);
                // calcula quantos dados retornaram
                $total = mysql_num_rows($dados);

                if($total > 0) { 
                    do {
                        $disc = $linha['disciplina_pk'];

                        $queryDis = "SELECT * FROM Disciplina WHERE disciplina_pk = '$disc'";

                        // executa a query
                        $dadosDis = mysql_query($queryDis, $con) or die(mysql_error());
                        // transforma os dados em um array
                        $linhaDis = mysql_fetch_assoc($dadosDis);
                        // calcula quantos dados retornaram
                        $totalDis = mysql_num_rows($dadosDis);

                        do { 
                            $disciplinas[] = array('disciplina_pk' => $linhaDis['disciplina_pk'],
                                                   'disciplina_nome' => $linhaDis['disciplina_nome'],
                                                   'selected' => false
                                            );
                        }while($linhaDis = mysql_fetch_assoc($dadosDis));
                    // finaliza o loop que vai mostrar os dados
                    }while($linha = mysql_fetch_assoc($dados));
                    $response = array('docente_pk' => $docente_pk, 'docente_nome' => $docente_nome, 'info' => array('disciplinas' => $disciplinas));
                    die(json_encode($response));
                }
            }
        } else {
            $response["success"] = 0;
            $response["message"] = "usuario ou senha invalido";
            die(json_encode($response));
        }
    } else{
        $response["success"] = 0;
        $response["message"] = " Um ou mais campos estao vazios ";
        die(json_encode($response));
    }
  
    mysql_close();
?>