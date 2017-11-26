<?php
/* @var $this PomenController */
/* @var $model Pomen */

$this->breadcrumbs=array(
	'Pomens'=>array('index'),
	'Kreiraj',
);

$this->menu=array(
	array('label'=>'Zlistaj Pomen', 'url'=>array('index')),
	array('label'=>'Urejaj Pomen', 'url'=>array('admin')),
);
?>

<h1>Create Pomen</h1>

<?php $this->renderPartial('_form', array('model'=>$model)); ?>