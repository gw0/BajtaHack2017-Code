<?php
/* @var $this MeritevController */
/* @var $dataProvider CActiveDataProvider */

$this->breadcrumbs=array(
	'Meritevs',
);

$this->menu=array(
	array('label'=>'Kreiraj Meritev', 'url'=>array('create')),
	array('label'=>'Urejaj Meritev', 'url'=>array('admin')),
);
?>

<h1>Meritevs</h1>

<?php $this->widget('zii.widgets.CListView', array(
	'dataProvider'=>$dataProvider,
	'itemView'=>'_view',
)); ?>
