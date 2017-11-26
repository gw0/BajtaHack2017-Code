<?php
/* @var $this SenzorController */
/* @var $model Senzor */

$this->breadcrumbs=array(
	'Senzors'=>array('index'),
	$model->ID=>array('view','id'=>$model->ID),
	'Update',
);

$this->menu=array(
	array('label'=>'Zlistaj Senzor', 'url'=>array('index')),
	array('label'=>'Kreiraj Senzor', 'url'=>array('create')),
	array('label'=>'Poglej Senzor', 'url'=>array('view', 'id'=>$model->ID)),
	array('label'=>'Urejaj Senzor', 'url'=>array('admin')),
);
?>

<h1>Update Senzor <?php echo $model->ID; ?></h1>

<?php $this->renderPartial('_form', array('model'=>$model)); ?>