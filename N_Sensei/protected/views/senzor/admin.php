<?php
/* @var $this SenzorController */
/* @var $model Senzor */

$this->breadcrumbs=array(
	'Senzors'=>array('index'),
	'Urejaj',
);

$this->menu=array(
	array('label'=>'Zlistaj Senzor', 'url'=>array('index')),
	array('label'=>'Kreiraj Senzor', 'url'=>array('create')),
);

Yii::app()->clientScript->registerScript('search', "
$('.search-button').click(function(){
	$('.search-form').toggle();
	return false;
});
$('.search-form form').submit(function(){
	$('#senzor-grid').yiiGridView('update', {
		data: $(this).serialize()
	});
	return false;
});
");
?>

<h1>Senzorji</h1>




<div class="search-form" style="display:none">
<?php $this->renderPartial('_search',array(
	'model'=>$model,
)); ?>
</div><!-- search-form -->

<?php $this->widget('zii.widgets.grid.CGridView', array(
	'id'=>'senzor-grid',
	'dataProvider'=>$model->search(),
	'filter'=>$model,
	'columns'=>array(
		//'ID',
		'NAZIV',
		//'NAPRAVA_ID',
		array('header' => 'Naprava', 'value' => '$data->naprava->NAZIV', 'htmlOptions'=>array('width'=>'10'),),
		array('header' => 'Tip', 'value' => '$data->tip->NAZIV', 'htmlOptions'=>array('width'=>'10'),),
		array('header' => 'Naslov', 'value' => '$data->NASLOV', 'htmlOptions'=>array('width'=>'10'),),
		array(
			'class'=>'CButtonColumn',
		),
	),
)); ?>
