<?php
/* @var $this TipController */
/* @var $model Tip */

$this->breadcrumbs=array(
	'Tips'=>array('index'),
	'Kreiraj',
);

$this->menu=array(
	array('label'=>'Zlistaj Tip', 'url'=>array('index')),
	array('label'=>'Urejaj Tip', 'url'=>array('admin')),
);
?>

<h1>Create Tip</h1>

<?php $this->renderPartial('_form', array('model'=>$model)); ?>