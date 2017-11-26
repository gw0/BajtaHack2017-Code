<?php
/* @var $this SenzorController */
/* @var $dataProvider CActiveDataProvider */

$this->breadcrumbs=array(
	'Senzors',
);

$this->menu=array(
	array('label'=>'Kreiraj Senzor', 'url'=>array('create')),
	array('label'=>'Urejaj Senzor', 'url'=>array('admin')),
);
?>

<h1>Senzors</h1>

<?php $this->widget('zii.widgets.CListView', array(
	'dataProvider'=>$dataProvider,
	'itemView'=>'_view',
)); ?>
