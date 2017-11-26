<?php
/* @var $this SiteController */

$this->pageTitle=Yii::app()->name;
?>

<?php Yii::app()->clientScript->registerCoreScript("jquery"); ?>

<?php echo CHtml::image(Yii::app()->request->baseUrl.'/images/sensei.jpg',
      '',
      array('width'=>'100%', 'title'=>'Sensei')); ?>

<script type="text/javascript">
	//$.post( "/phy/gpio/alloc")
	//  .done(function( data ) {
	//    alert( "Data Loaded: " + data );
	//  });
	root_n1 = "https://n1-wlan0.srm.bajtahack.si:14100/";
	root_n2 = "https://n2-wlan0.srm.bajtahack.si:14200/";
	root_n3 = "https://n3-wlan0.srm.bajtahack.si:14300/";

	request = root_n3 + 'phy/gpio';

	$.get(request , function( data ) {
	  //$( ".result" ).html( data );
	  //alert(data);
	});

	function addData(sensorId, value) {
		//alert();
		$("#content").load("addData/?sensorId=" + sensorId + "&value=" + value);
	}

	$("document").ready(function() {
	    $("#myButton").click(function() {
	         addData(1, 2);
	    });
	});

</script>
