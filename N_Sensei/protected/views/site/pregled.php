<?php
/* @var $this SiteController */

$this->pageTitle=Yii::app()->name.' -  Statistika';
?>

<?php 
Yii::app()->clientScript->coreScriptPosition = CClientScript::POS_BEGIN;
Yii::app()->clientScript->registerCoreScript("jquery"); 
?>


<script type="text/javascript">

  function putRequest(request, data) {
    $.ajax({
       url: request,
       data: data,
       async: false,
       type: 'PUT',
       success: function(response) {
       }
    });
  }

  function getRequest(request, divId, tipId) {
    $.get(request , function( data ) {
      value = parseInt(data.replace(/\"/g, ""), 16);
      ret = 0;
      // temperatura
      if (tipId==1) ret = (parseFloat(value) / 65536 * 165 ) - 40;
      // vlažnost
      if (tipId==2) ret = (parseFloat(value) / 65536 * 100 ); 
      // svetilnost
      if (tipId==3) ret = parseFloat(value / 10777216);


      //  15777216 
      //1761612032

      $("#senzor_" + divId).html(ret.toFixed(2));
    });
  }

  $("document").ready(function() {

  });
</script>


<?php
  
  foreach (Naprava::model()->findAll() as $naprava) {
    if (count(Senzor::model()->findAllByAttributes(array("NAPRAVA_ID"=>$naprava->ID)))==0) continue;
    echo '<b>'.$naprava->NAZIV.'</b><br/>';
    foreach (Senzor::model()->findAllByAttributes(array("NAPRAVA_ID"=>$naprava->ID)) as $senzor) {

      echo CHtml::hiddenField('napravaId_'.$naprava->ID, $naprava->ID, array('id' => 'napravaId_'.$naprava->ID));
      echo CHtml::hiddenField('senzorId_'.$senzor->ID, $senzor->ID, array('id' => 'senzorId_'.$senzor->ID));
      echo CHtml::hiddenField('requestId_'.$senzor->ID, $naprava->NASLOV.$senzor->NASLOV, array('id' => 'requestId_'.$senzor->ID));

      if ($senzor->tip->PROTOKOL == 0) {
        echo '<script type="text/javascript">',
             'getRequest("'.$naprava->NASLOV.$senzor->NASLOV.'", "'.$senzor->ID.'", "'.$senzor->TIP_ID.'");',
             '</script>'
        ;
      } else  if ($senzor->tip->PROTOKOL == 1) {
        
        echo "<script type='text/javascript'>",
             "putRequest('".$naprava->NASLOV.$senzor->NASLOV."/value', '".$senzor->tip->DATA."');",
             "</script>"
        ;

        echo '<script type="text/javascript">',
             'getRequest("'.$naprava->NASLOV.$senzor->NASLOV.'/value", "'.$senzor->ID.'", "'.$senzor->TIP_ID.'");',
             '</script>'
        ;
      }
      echo '<div style="float: left;">';
      echo ' '.$senzor->NAZIV.': ';
      echo '</div>';
      echo '<div id="senzor_'.$senzor->ID.'" style="float: left; font-weight: bold;">';
      echo '</div>';
      echo '<div style="float: left;">';
      echo $senzor->tip->EM;
      echo '</div>';
      echo '<div id="pomen_'.$senzor->ID.'" style="float: left;">';
      echo '</div>';
      echo '<div class="clear"></div>';
    }
  }

?>
