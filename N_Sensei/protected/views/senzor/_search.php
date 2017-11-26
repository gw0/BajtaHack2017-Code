<?php
/* @var $this SenzorController */
/* @var $model Senzor */
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
		<?php echo $form->label($model,'NAZIV'); ?>
		<?php echo $form->textField($model,'NAZIV',array('size'=>60,'maxlength'=>300)); ?>
	</div>

	<div class="row">
		<?php echo $form->label($model,'NAPRAVA_ID'); ?>
		<?php echo $form->textField($model,'NAPRAVA_ID'); ?>
	</div>

	<div class="row">
		<?php echo $form->label($model,'TIP_ID'); ?>
		<?php echo $form->textField($model,'TIP_ID'); ?>
	</div>

	<div class="row">
		<?php echo $form->label($model,'NASLOV'); ?>
		<?php echo $form->textField($model,'NASLOV'); ?>
	</div>

	<div class="row buttons">
		<?php echo CHtml::submitButton('Search'); ?>
	</div>

<?php $this->endWidget(); ?>

</div><!-- search-form -->