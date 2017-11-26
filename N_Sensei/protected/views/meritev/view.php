<?php
/* @var $this MeritevController */
/* @var $model Meritev */

$this->breadcrumbs=array(
	'Meritevs'=>array('index'),
	$model->ID,
);

$this->menu=array(
	array('label'=>'Zlistaj Meritev', 'url'=>array('index')),
	array('label'=>'Kreiraj Meritev', 'url'=>array('create')),
	array('label'=>'Update Meritev', 'url'=>array('update', 'id'=>$model->ID)),
	array('label'=>'Izbriši Meritev', 'url'=>'#', 'linkOptions'=>array('submit'=>array('delete','id'=>$model->ID),'confirm'=>'Are you sure you want to delete this item?')),
	array('label'=>'Urejaj Meritev', 'url'=>array('admin')),
);
?>

<h1>View Meritev #<?php echo $model->ID; ?></h1>

<?php $this->widget('zii.widgets.CDetailView', array(
	'data'=>$model,
	'attributes'=>array(
		'ID',
		'SENZOR_ID',
		'VALUE',
		'CREATED',
	),
)); ?>
