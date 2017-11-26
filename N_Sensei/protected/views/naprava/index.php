<?php
/* @var $this NapravaController */
/* @var $dataProvider CActiveDataProvider */

$this->breadcrumbs=array(
	'Napravas',
);

$this->menu=array(
	array('label'=>'Kreiraj Naprava', 'url'=>array('create')),
	array('label'=>'Urejaj Naprava', 'url'=>array('admin')),
);
?>

<h1>Napravas</h1>

<?php $this->widget('zii.widgets.CListView', array(
	'dataProvider'=>$dataProvider,
	'itemView'=>'_view',
)); ?>
