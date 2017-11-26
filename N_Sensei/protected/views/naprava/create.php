<?php
/* @var $this NapravaController */
/* @var $model Naprava */

$this->breadcrumbs=array(
	'Napravas'=>array('index'),
	'Kreiraj',
);

$this->menu=array(
	array('label'=>'Zlistaj Naprava', 'url'=>array('index')),
	array('label'=>'Urejaj Naprava', 'url'=>array('admin')),
);
?>

<h1>Create Naprava</h1>

<?php $this->renderPartial('_form', array('model'=>$model)); ?>