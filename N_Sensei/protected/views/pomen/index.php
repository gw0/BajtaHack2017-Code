<?php
/* @var $this PomenController */
/* @var $dataProvider CActiveDataProvider */

$this->breadcrumbs=array(
	'Pomens',
);

$this->menu=array(
	array('label'=>'Kreiraj Pomen', 'url'=>array('create')),
	array('label'=>'Urejaj Pomen', 'url'=>array('admin')),
);
?>

<h1>Pomens</h1>

<?php $this->widget('zii.widgets.CListView', array(
	'dataProvider'=>$dataProvider,
	'itemView'=>'_view',
)); ?>
