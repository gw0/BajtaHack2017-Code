<?php
/* @var $this TipController */
/* @var $model Tip */

$this->breadcrumbs=array(
	'Tips'=>array('index'),
	$model->ID,
);

$this->menu=array(
	array('label'=>'Zlistaj Tip', 'url'=>array('index')),
	array('label'=>'Kreiraj Tip', 'url'=>array('create')),
	array('label'=>'Update Tip', 'url'=>array('update', 'id'=>$model->ID)),
	array('label'=>'Izbriši Tip', 'url'=>'#', 'linkOptions'=>array('submit'=>array('delete','id'=>$model->ID),'confirm'=>'Are you sure you want to delete this item?')),
	array('label'=>'Urejaj Tip', 'url'=>array('admin')),
);
?>

<h1>View Tip #<?php echo $model->ID; ?></h1>

<?php $this->widget('zii.widgets.CDetailView', array(
	'data'=>$model,
	'attributes'=>array(
		'ID',
		'NAZIV',
		'EM',
		'PROTOKOL',
		'DATA',
	),
)); ?>
