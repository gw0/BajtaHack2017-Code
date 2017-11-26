<?php
/* @var $this NapravaController */
/* @var $model Naprava */

$this->breadcrumbs=array(
	'Napravas'=>array('index'),
	$model->ID,
);

$this->menu=array(
	array('label'=>'Zlistaj Naprava', 'url'=>array('index')),
	array('label'=>'Kreiraj Naprava', 'url'=>array('create')),
	array('label'=>'Update Naprava', 'url'=>array('update', 'id'=>$model->ID)),
	array('label'=>'Izbriši Naprava', 'url'=>'#', 'linkOptions'=>array('submit'=>array('delete','id'=>$model->ID),'confirm'=>'Are you sure you want to delete this item?')),
	array('label'=>'Urejaj Naprava', 'url'=>array('admin')),
);
?>

<h1>View Naprava #<?php echo $model->ID; ?></h1>

<?php $this->widget('zii.widgets.CDetailView', array(
	'data'=>$model,
	'attributes'=>array(
		'ID',
		'NAZIV',
		'NASLOV',
	),
)); ?>
