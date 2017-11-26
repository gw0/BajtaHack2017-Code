<?php
/* @var $this TipController */
/* @var $model Tip */

$this->breadcrumbs=array(
	'Tips'=>array('index'),
	$model->ID=>array('view','id'=>$model->ID),
	'Update',
);

$this->menu=array(
	array('label'=>'Zlistaj Tip', 'url'=>array('index')),
	array('label'=>'Kreiraj Tip', 'url'=>array('create')),
	array('label'=>'Poglej Tip', 'url'=>array('view', 'id'=>$model->ID)),
	array('label'=>'Urejaj Tip', 'url'=>array('admin')),
);
?>

<h1>Update Tip <?php echo $model->ID; ?></h1>

<?php $this->renderPartial('_form', array('model'=>$model)); ?>