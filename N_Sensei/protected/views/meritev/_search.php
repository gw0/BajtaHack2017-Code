<?php
/* @var $this MeritevController */
/* @var $model Meritev */
/* @var $form CActiveForm */
?>

<div class="wide form">

<?php $form=$this->beginWidget('CActiveForm', array(
	'action'=>Yii::app()->createUrl($this->route),
	'method'=>'get',
)); ?>

	<div class="row">
		<?php echo $form->label($model,'ID'); ?>
		<?php echo $form->textField($model,'ID'); ?>
	</div>

	<div class="row">
		<?php echo $form->label($model,'SENZOR_ID'); ?>
		<?php echo $form->textField($model,'SENZOR_ID'); ?>
	</div>

	<div class="row">
		<?php echo $form->label($model,'VALUE'); ?>
		<?php echo $form->textField($model,'VALUE',array('size'=>60,'maxlength'=>100)); ?>
	</div>

	<div class="row">
		<?php echo $form->label($model,'CREATED'); ?>
		<?php echo $form->textField($model,'CREATED'); ?>
	</div>

	<div class="row buttons">
		<?php echo CHtml::submitButton('Search'); ?>
	</div>

<?php $this->endWidget(); ?>

</div><!-- search-form -->