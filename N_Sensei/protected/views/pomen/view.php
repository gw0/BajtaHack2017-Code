<?php
/* @var $this PomenController */
/* @var $model Pomen */

$this->breadcrumbs=array(
	'Pomens'=>array('index'),
	$model->ID,
);

$this->menu=array(
	array('label'=>'Zlistaj Pomen', 'url'=>array('index')),
	array('label'=>'Kreiraj Pomen', 'url'=>array('create')),
	array('label'=>'Update Pomen', 'url'=>array('update', 'id'=>$model->ID)),
	array('label'=>'Izbriši Pomen', 'url'=>'#', 'linkOptions'=>array('submit'=>array('delete','id'=>$model->ID),'confirm'=>'Are you sure you want to delete this item?')),
	array('label'=>'Urejaj Pomen', 'url'=>array('admin')),
);
?>

<h1>View Pomen #<?php echo $model->ID; ?></h1>

<?php $this->widget('zii.widgets.CDetailView', array(
	'data'=>$model,
	'attributes'=>array(
		'ID',
		'SENZOR_ID',
		'POMEN',
		'MIN_VREDNOST',
		'MAX_VREDNOST',
	),
)); ?>
