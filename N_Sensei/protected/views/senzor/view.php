<?php
/* @var $this SenzorController */
/* @var $model Senzor */

$this->breadcrumbs=array(
	'Senzors'=>array('index'),
	$model->ID,
);

$this->menu=array(
	array('label'=>'Zlistaj Senzor', 'url'=>array('index')),
	array('label'=>'Kreiraj Senzor', 'url'=>array('create')),
	array('label'=>'Update Senzor', 'url'=>array('update', 'id'=>$model->ID)),
	array('label'=>'Izbriši Senzor', 'url'=>'#', 'linkOptions'=>array('submit'=>array('delete','id'=>$model->ID),'confirm'=>'Are you sure you want to delete this item?')),
	array('label'=>'Urejaj Senzor', 'url'=>array('admin')),
);
?>

<h1>View Senzor #<?php echo $model->ID; ?></h1>

<?php $this->widget('zii.widgets.CDetailView', array(
	'data'=>$model,
	'attributes'=>array(
		'ID',
		'NAZIV',
		'NAPRAVA_ID',
		'TIP_ID',
		'NASLOV',
	),
)); ?>
