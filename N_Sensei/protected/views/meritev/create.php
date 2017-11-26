<?php
/* @var $this MeritevController */
/* @var $model Meritev */

$this->breadcrumbs=array(
	'Meritve'=>array('index'),
	'Kreiraj',
);

$this->menu=array(
	array('label'=>'Zlistaj Meritev', 'url'=>array('index')),
	array('label'=>'Urejaj Meritev', 'url'=>array('admin')),
);
?>

<h1>Create Meritev</h1>

<?php $this->renderPartial('_form', array('model'=>$model)); ?>