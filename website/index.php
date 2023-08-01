<!DOCTYPE html>
<html>
    <?php include('header/header.php') ?>
    <head>
        <link rel="stylesheet" href="index.css">
    </head>
    <body>    
        <br>
        <br>
        <br>
        <br>
        <br>
        
        <!-- Fetch news from steam api, convert BBCode to HTML, write array of posts to correct format. -->
        <?php 

            function bbcodeHtml($str) {
                $str = str_replace("\\n", "<br>", $str);
                $str = str_replace("[h1]", "<h1>", $str);
                $str = str_replace("[/h1]", "</h1>", $str);
                $str = str_replace("[h2]", "<h2>", $str);
                $str = str_replace("[/h2]", "</h2>", $str);
                $str = str_replace("[h3]", "<h3>", $str);
                $str = str_replace("[/h3]", "</h3>", $str);
                $str = str_replace("[list]", "<ul>", $str);
                $str = str_replace("[/list]", "</ul>", $str);
                $str = str_replace("[*]", "</li><li>", $str);
                $str = str_replace("[i]", "<i>", $str);
                $str = str_replace("[/i]", "</i>", $str);
                $str = str_replace("[b]", "<b>", $str);
                $str = str_replace("[/b]", "</b>", $str);
                $str = str_replace("[u]", "<u>", $str);
                $str = str_replace("[/u]", "</u>", $str);
                $str = str_replace("[img]", "<img src=\"", $str);
                $str = str_replace("[/img]", "\">", $str);

                return $str;
            }
            
            $json = file_get_contents("https://api.steampowered.com/ISteamNews/GetNewsForApp/v2/?appid=2090230&count=100");
            $json = str_replace("\\n", "<br>", $json);
            $json = json_decode($json, true);

            foreach ($json['appnews']['newsitems'] as $post) {
                $title = $post['title'];
                $url = $post['url'];
                $content = $post['contents'];
                $date = $post['date'];

                echo "    <div class=\"content\"> 
                    <div class=\"newspanel\">
                        <a href=".$url." target=\"_blank\" class=\"newstitle\">".$title."</i></a>
                        <a class=\"newstypedate\"><i class=\"fa-sharp fa-solid fa-hammer\"></i> Update |  <i class=\"fa-solid fa-clock\"></i> ".date('H:i m/d/Y', $date)."</a>
                        <br>
                        <br>
                        <div class=\"newscontent\">
                            ".bbcodeHtml($content)."
                        </div>
                    </div>
            </div>\n        ";
            }

        ?>

        <video playsinline autoplay muted loop poster="assets/background.png" id="bgvid">
            <source src="assets/background.mp4" type="video/mp4">
        </video>
    </body>
</html>