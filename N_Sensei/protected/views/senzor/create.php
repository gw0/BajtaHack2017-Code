<?php
/* @var $this SenzorController */
/* @var $model Senzor */

$this->breadcrumbs=array(
	'Senzors'=>array('index'),
	'Kreiraj',
);

$this->menu=array(
	array('label'=>'Zlistaj Senzor', 'url'=>array('index')),
	array('label'=>'Urejaj Senzor', 'url'=>array('admin')),
);
?>

<h1>Create Senzor</h1>

<?php $this->renderPartial('_form', array('model'=>$model)); ?>