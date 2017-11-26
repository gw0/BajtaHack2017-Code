<?php
/* @var $this PomenController */
/* @var $model Pomen */

$this->breadcrumbs=array(
	'Pomens'=>array('index'),
	$model->ID=>array('view','id'=>$model->ID),
	'Update',
);

$this->menu=array(
	array('label'=>'Zlistaj Pomen', 'url'=>array('index')),
	array('label'=>'Kreiraj Pomen', 'url'=>array('create')),
	array('label'=>'Poglej Pomen', 'url'=>array('view', 'id'=>$model->ID)),
	array('label'=>'Urejaj Pomen', 'url'=>array('admin')),
);
?>

<h1>Update Pomen <?php echo $model->ID; ?></h1>

<?php $this->renderPartial('_form', array('model'=>$model)); ?>