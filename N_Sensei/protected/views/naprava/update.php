<?php
/* @var $this NapravaController */
/* @var $model Naprava */

$this->breadcrumbs=array(
	'Napravas'=>array('index'),
	$model->ID=>array('view','id'=>$model->ID),
	'Update',
);

$this->menu=array(
	array('label'=>'Zlistaj Naprava', 'url'=>array('index')),
	array('label'=>'Kreiraj Naprava', 'url'=>array('create')),
	array('label'=>'Poglej Naprava', 'url'=>array('view', 'id'=>$model->ID)),
	array('label'=>'Urejaj Naprava', 'url'=>array('admin')),
);
?>

<h1>Update Naprava <?php echo $model->ID; ?></h1>

<?php $this->renderPartial('_form', array('model'=>$model)); ?>