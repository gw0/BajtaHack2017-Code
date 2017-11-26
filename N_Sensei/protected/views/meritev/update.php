<?php
/* @var $this MeritevController */
/* @var $model Meritev */

$this->breadcrumbs=array(
	'Meritevs'=>array('index'),
	$model->ID=>array('view','id'=>$model->ID),
	'Update',
);

$this->menu=array(
	array('label'=>'Zlistaj Meritev', 'url'=>array('index')),
	array('label'=>'Kreiraj Meritev', 'url'=>array('create')),
	array('label'=>'Poglej Meritev', 'url'=>array('view', 'id'=>$model->ID)),
	array('label'=>'Urejaj Meritev', 'url'=>array('admin')),
);
?>

<h1>Update Meritev <?php echo $model->ID; ?></h1>

<?php $this->renderPartial('_form', array('model'=>$model)); ?>