<?php
/* @var $this PomenController */
/* @var $model Pomen */

$this->breadcrumbs=array(
	'Pomens'=>array('index'),
	'Urejaj',
);

$this->menu=array(
	array('label'=>'Zlistaj Pomen', 'url'=>array('index')),
	array('label'=>'Kreiraj Pomen', 'url'=>array('create')),
);

Yii::app()->clientScript->registerScript('search', "
$('.search-button').click(function(){
	$('.search-form').toggle();
	return false;
});
$('.search-form form').submit(function(){
	$('#pomen-grid').yiiGridView('update', {
		data: $(this).serialize()
	});
	return false;
});
");
?>

<h1>Pomeni</h1>




<div class="search-form" style="display:none">
<?php $this->renderPartial('_search',array(
	'model'=>$model,
)); ?>
</div><!-- search-form -->

<?php $this->widget('zii.widgets.grid.CGridView', array(
	'id'=>'pomen-grid',
	'dataProvider'=>$model->search(),
	'filter'=>$model,
	'columns'=>array(
		'ID',
		'SENZOR_ID',
		'POMEN',
		'MIN_VREDNOST',
		'MAX_VREDNOST',
		array(
			'class'=>'CButtonColumn',
		),
	),
)); ?>
