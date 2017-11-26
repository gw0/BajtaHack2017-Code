<?php
/* @var $this NapravaController */
/* @var $model Naprava */

$this->breadcrumbs=array(
	'Napravas'=>array('index'),
	'Urejaj',
);

$this->menu=array(
	array('label'=>'Zlistaj Naprava', 'url'=>array('index')),
	array('label'=>'Kreiraj Naprava', 'url'=>array('create')),
);

Yii::app()->clientScript->registerScript('search', "
$('.search-button').click(function(){
	$('.search-form').toggle();
	return false;
});
$('.search-form form').submit(function(){
	$('#naprava-grid').yiiGridView('update', {
		data: $(this).serialize()
	});
	return false;
});
");
?>

<h1>Naprave</h1>




<div class="search-form" style="display:none">
<?php $this->renderPartial('_search',array(
	'model'=>$model,
)); ?>
</div><!-- search-form -->

<?php $this->widget('zii.widgets.grid.CGridView', array(
	'id'=>'naprava-grid',
	'dataProvider'=>$model->search(),
	'filter'=>$model,
	'columns'=>array(
		//'ID',
		'NAZIV',
		'NASLOV',
		array(
			'class'=>'CButtonColumn',
		),
	),
)); ?>
