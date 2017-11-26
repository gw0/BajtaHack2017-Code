<?php
/* @var $this MeritevController */
/* @var $model Meritev */

$this->breadcrumbs=array(
	'Meritve'=>array('index'),
	'Urejaj',
);

$this->menu=array(
	array('label'=>'Zlistaj Meritev', 'url'=>array('index')),
	array('label'=>'Kreiraj Meritev', 'url'=>array('create')),
);

Yii::app()->clientScript->registerScript('search', "
$('.search-button').click(function(){
	$('.search-form').toggle();
	return false;
});
$('.search-form form').submit(function(){
	$('#meritev-grid').yiiGridView('update', {
		data: $(this).serialize()
	});
	return false;
});
");
?>

<h1>Meritve</h1>



<div class="search-form" style="display:none">
<?php $this->renderPartial('_search',array(
	'model'=>$model,
)); ?>
</div><!-- search-form -->

<?php $this->widget('zii.widgets.grid.CGridView', array(
	'id'=>'meritev-grid',
	'dataProvider'=>$model->search(),
	'filter'=>$model,
	'columns'=>array(
		//'ID',
		//'SENZOR_ID',
		array('header' => 'Naprava', 'value' => '$data->senzor->naprava->NAZIV', 'htmlOptions'=>array('width'=>'10'),),
		array('header' => 'Senzor', 'value' => '$data->senzor->NAZIV', 'htmlOptions'=>array('width'=>'10'),),
		array('header' => 'Vrednost', 'value' => '$data->VALUE', 'htmlOptions'=>array('width'=>'10'),),
		array('header' => 'Čas', 'value' => '$data->CREATED', 'htmlOptions'=>array('width'=>'10'),),
		array(
			'class'=>'CButtonColumn',
		),
	),
)); ?>
